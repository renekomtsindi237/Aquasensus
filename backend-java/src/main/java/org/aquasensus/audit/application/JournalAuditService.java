package org.aquasensus.audit.application;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JournalAuditService {

    private final JdbcTemplate jdbc;

    public JournalAuditService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Transactional
    public void enregistrer(UUID acteurId, String action, String entite, String entiteId, String avant, String apres) {
        jdbc.update(
                """
                INSERT INTO journal_audit (id, horodatage, acteur_id, action, entite, entite_id, avant, apres)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """,
                UUID.randomUUID(),
                java.sql.Timestamp.from(Instant.now()),
                acteurId,
                action,
                entite,
                entiteId,
                masquer(avant),
                masquer(apres));
    }

    @Transactional(readOnly = true)
    public List<Entree> lister(String entite, UUID acteurId, Instant debut, Instant fin) {
        Instant d = debut == null ? Instant.EPOCH : debut;
        Instant f = fin == null ? Instant.now() : fin;
        String sql =
                """
                SELECT id, horodatage, acteur_id, action, entite, entite_id, avant, apres
                FROM journal_audit WHERE horodatage >= ? AND horodatage < ?
                """;
        java.util.ArrayList<Object> args = new java.util.ArrayList<>();
        args.add(d);
        args.add(f);
        if (entite != null && !entite.isBlank()) {
            sql += " AND entite = ? ";
            args.add(entite);
        }
        if (acteurId != null) {
            sql += " AND acteur_id = ? ";
            args.add(acteurId);
        }
        sql += " ORDER BY horodatage DESC LIMIT 200";
        return jdbc.query(
                sql,
                (rs, row) -> new Entree(
                        (UUID) rs.getObject("id"),
                        rs.getTimestamp("horodatage").toInstant(),
                        (UUID) rs.getObject("acteur_id"),
                        rs.getString("action"),
                        rs.getString("entite"),
                        rs.getString("entite_id"),
                        rs.getString("avant"),
                        rs.getString("apres")),
                args.toArray());
    }

    static String masquer(String texte) {
        if (texte == null) {
            return null;
        }
        return texte.replaceAll("(?i)(telephone|phone|msisdn)\"\\s*:\\s*\"[^\"]+\"", "$1\":\"***\"");
    }

    public record Entree(
            UUID id,
            Instant horodatage,
            UUID acteurId,
            String action,
            String entite,
            String entiteId,
            String avant,
            String apres) {}
}
