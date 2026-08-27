package org.aquasensus.analytics.application;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.aquasensus.registry.domain.EtatPointEau;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TableauBordService {

    private final JdbcTemplate jdbc;

    public TableauBordService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Transactional(readOnly = true)
    public KpiAgreges aggregats(Instant debut, Instant fin, UUID localiteId, UUID comiteId) {
        Instant d = debut == null ? Instant.EPOCH : debut;
        Instant f = fin == null ? Instant.now() : fin;
        Map<String, Long> parEtat = new LinkedHashMap<>();
        for (EtatPointEau e : EtatPointEau.values()) {
            parEtat.put(e.name(), 0L);
        }
        String filtreOuvrage = " WHERE p.actif = TRUE ";
        List<Object> argsEtat = new ArrayList<>();
        if (comiteId != null) {
            filtreOuvrage += " AND p.comite_id = ? ";
            argsEtat.add(comiteId);
        }
        if (localiteId != null) {
            filtreOuvrage += " AND p.localite_id = ? ";
            argsEtat.add(localiteId);
        }
        jdbc.query(
                "SELECT p.etat, COUNT(*) AS n FROM point_eau p" + filtreOuvrage + " GROUP BY p.etat",
                argsEtat.toArray(),
                (rs, row) -> {
                    parEtat.put(rs.getString("etat"), rs.getLong("n"));
                    return null;
                });
        long horsService = parEtat.getOrDefault(EtatPointEau.HORS_SERVICE.name(), 0L);
        long actifsSuivis = parEtat.values().stream().mapToLong(Long::longValue).sum() - horsService;

        List<Long> retablissements = jdbc.query(
                """
                SELECT i.temps_retablissement_minutes FROM intervention i
                JOIN point_eau p ON p.id = i.point_eau_id
                WHERE i.statut = 'CLOTUREE' AND i.temps_retablissement_minutes IS NOT NULL
                  AND i.cloturee_le >= ? AND i.cloturee_le < ?
                """
                        + extraJoin(comiteId, localiteId),
                (rs, row) -> rs.getLong(1),
                paramsPeriode(d, f, comiteId, localiteId));

        List<Long> delaisAffectation = jdbc.query(
                """
                SELECT i.ouverte_le, i.affectee_le FROM intervention i
                JOIN point_eau p ON p.id = i.point_eau_id
                WHERE i.affectee_le IS NOT NULL
                  AND i.affectee_le >= ? AND i.affectee_le < ?
                """
                        + extraJoin(comiteId, localiteId),
                (rs, row) -> Duration.between(rs.getTimestamp(1).toInstant(), rs.getTimestamp(2).toInstant())
                        .toMinutes(),
                paramsPeriode(d, f, comiteId, localiteId));

        Long alertesActives = jdbc.queryForObject(
                "SELECT COUNT(*) FROM alerte WHERE statut = 'ACTIVE'", Long.class);
        Long interventionsEnCours = jdbc.queryForObject(
                """
                SELECT COUNT(*) FROM intervention
                WHERE statut IN ('OUVERTE','AFFECTEE','EN_COURS','SUSPENDUE','REALISEE')
                """,
                Long.class);

        List<Panne> pannes = jdbc.query(
                """
                SELECT h.point_eau_id, h.survenu_le FROM historique_etat h
                JOIN point_eau p ON p.id = h.point_eau_id
                WHERE h.etat_nouveau = 'EN_PANNE' AND h.survenu_le >= ? AND h.survenu_le < ?
                """
                        + extraJoin(comiteId, localiteId),
                (rs, row) -> new Panne((UUID) rs.getObject(1), rs.getTimestamp(2).toInstant()),
                paramsPeriode(d, f, comiteId, localiteId));
        List<AlerteLegere> alerteHist = jdbc.query(
                "SELECT point_eau_id, emise_le, horizon_jours FROM alerte",
                (rs, row) -> new AlerteLegere(
                        (UUID) rs.getObject(1), rs.getTimestamp(2).toInstant(), rs.getInt(3)));
        long anticipees = 0;
        for (Panne panne : pannes) {
            boolean ok = alerteHist.stream()
                    .anyMatch(a -> a.pointEauId.equals(panne.pointEauId)
                            && !a.emiseLe.isAfter(panne.survenuLe)
                            && !panne.survenuLe.isAfter(a.emiseLe.plus(Duration.ofDays(a.horizonJours))));
            if (ok) {
                anticipees++;
            }
        }
        double tauxAnticipation = pannes.isEmpty() ? 0.0 : (double) anticipees / pannes.size();

        return new KpiAgreges(
                percentile(retablissements, 0.5),
                percentile(retablissements, 0.9),
                Map.copyOf(parEtat),
                horsService,
                actifsSuivis,
                alertesActives == null ? 0 : alertesActives,
                interventionsEnCours == null ? 0 : interventionsEnCours,
                percentile(delaisAffectation, 0.5),
                tauxAnticipation,
                "HORS_SERVICE exclu des KPI de disponibilité (RG-12). Aucun volume d'eau.");
    }

    private static String extraJoin(UUID comiteId, UUID localiteId) {
        String extra = "";
        if (comiteId != null) {
            extra += " AND p.comite_id = ? ";
        }
        if (localiteId != null) {
            extra += " AND p.localite_id = ? ";
        }
        return extra;
    }

    private static Object[] paramsPeriode(Instant d, Instant f, UUID comiteId, UUID localiteId) {
        List<Object> p = new ArrayList<>();
        p.add(java.sql.Timestamp.from(d));
        p.add(java.sql.Timestamp.from(f));
        if (comiteId != null) {
            p.add(comiteId);
        }
        if (localiteId != null) {
            p.add(localiteId);
        }
        return p.toArray();
    }

    static Long percentile(List<Long> valeurs, double q) {
        if (valeurs.isEmpty()) {
            return null;
        }
        List<Long> tri = new ArrayList<>(valeurs);
        tri.sort(Comparator.naturalOrder());
        int idx = (int) Math.ceil(q * tri.size()) - 1;
        return tri.get(Math.max(0, Math.min(idx, tri.size() - 1)));
    }

    private record Panne(UUID pointEauId, Instant survenuLe) {}

    private record AlerteLegere(UUID pointEauId, Instant emiseLe, int horizonJours) {}

    public List<LigneBudget> budget(Instant debut, Instant fin, UUID localiteId, UUID comiteId) {
        Instant d = debut == null ? Instant.EPOCH : debut;
        Instant f = fin == null ? Instant.now() : fin;
        String sql =
                """
                SELECT p.comite_id,
                       COALESCE(SUM(i.cout_pieces), 0),
                       COALESCE(SUM(i.cout_main_oeuvre), 0)
                FROM intervention i
                JOIN point_eau p ON p.id = i.point_eau_id
                WHERE i.cloturee_le IS NOT NULL AND i.cloturee_le >= ? AND i.cloturee_le < ?
                """
                        + extraJoin(comiteId, localiteId)
                        + " GROUP BY p.comite_id";
        return jdbc.query(
                sql,
                (rs, row) -> new LigneBudget(
                        (UUID) rs.getObject(1),
                        rs.getBigDecimal(2),
                        rs.getBigDecimal(3)),
                paramsPeriode(d, f, comiteId, localiteId));
    }

    public record LigneBudget(UUID comiteId, java.math.BigDecimal coutPieces, java.math.BigDecimal coutMainOeuvre) {}

    public record KpiAgreges(
            Long retablissementMedianMinutes,
            Long retablissementP90Minutes,
            Map<String, Long> pointsParEtat,
            long horsServiceExclus,
            long ouvragesActifsHorsHorsService,
            long alertesActives,
            long interventionsEnCours,
            Long delaiAffectationMedianMinutes,
            double tauxAnticipation,
            String note) {}
}
