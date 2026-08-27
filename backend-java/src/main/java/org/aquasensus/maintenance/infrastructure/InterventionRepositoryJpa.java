package org.aquasensus.maintenance.infrastructure;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.aquasensus.maintenance.domain.CompteRendu;
import org.aquasensus.maintenance.domain.Intervention;
import org.aquasensus.maintenance.domain.InterventionRepository;
import org.aquasensus.maintenance.domain.MotifSuspension;
import org.aquasensus.maintenance.domain.OrigineIntervention;
import org.aquasensus.maintenance.domain.PieceRemplacee;
import org.aquasensus.maintenance.domain.StatutIntervention;
import org.aquasensus.maintenance.domain.TypeIntervention;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class InterventionRepositoryJpa implements InterventionRepository {

    private final InterventionJpa interventions;
    private final InterventionSignalementJpa liens;
    private final PieceRemplaceeJpa pieces;
    private final JdbcTemplate jdbc;

    public InterventionRepositoryJpa(
            InterventionJpa interventions,
            InterventionSignalementJpa liens,
            PieceRemplaceeJpa pieces,
            JdbcTemplate jdbc) {
        this.interventions = interventions;
        this.liens = liens;
        this.pieces = pieces;
        this.jdbc = jdbc;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Intervention> parId(UUID id) {
        return interventions.findById(id).map(this::versDomaine);
    }

    @Override
    @Transactional
    public Intervention enregistrer(Intervention i) {
        InterventionEntity e = interventions.findById(i.id()).orElseGet(InterventionEntity::new);
        if (e.getId() == null) {
            e.setId(i.id());
        }
        e.setReference(i.reference());
        e.setPointEauId(i.pointEauId());
        e.setType(i.type().name());
        e.setOrigine(i.origine().name());
        e.setTechnicienId(i.technicienId());
        e.setStatut(i.statut().name());
        e.setEcheanceSouhaitee(i.echeanceSouhaitee());
        e.setMotifSuspension(i.motifSuspension() == null ? null : i.motifSuspension().name());
        e.setMotifAnnulation(i.motifAnnulation());
        if (i.compteRendu() != null) {
            e.setDiagnostic(i.compteRendu().diagnostic());
            e.setCauseRacine(i.compteRendu().causeRacine());
            e.setActions(i.compteRendu().actions());
        }
        e.setCoutPieces(i.coutPieces());
        e.setOuverteLe(i.ouverteLe());
        e.setAffecteeLe(i.affecteeLe());
        e.setDemarreeLe(i.demarreeLe());
        e.setRealiseeLe(i.realiseeLe());
        e.setClotureeLe(i.clotureeLe());
        e.setTempsRetablissementMinutes(i.tempsRetablissementMinutes());
        e.setConfirmeeParId(i.confirmeeParId());
        e.setInterventionOrigineId(i.interventionOrigineId());
        interventions.saveAndFlush(e);
        i.synchroniserVersion(e.getVersion() == null ? 0 : e.getVersion());

        Set<UUID> deja = new HashSet<>();
        liens.findByInterventionId(i.id()).forEach(l -> deja.add(l.getSignalementId()));
        for (UUID sid : i.signalementIds()) {
            if (!deja.contains(sid)) {
                liens.save(new InterventionSignalementEntity(i.id(), sid));
            }
        }
        for (PieceRemplacee p : i.pieces()) {
            if (pieces.existsById(p.id())) {
                continue;
            }
            PieceRemplaceeEntity pe = new PieceRemplaceeEntity();
            pe.setId(p.id());
            pe.setInterventionId(i.id());
            pe.setReferencePiece(p.reference());
            pe.setLibelle(p.libelle());
            pe.setQuantite(p.quantite());
            pe.setCoutUnitaire(p.coutUnitaire());
            pieces.save(pe);
        }
        return i;
    }

    @Override
    public long prochaineReference() {
        try {
            Long n = jdbc.queryForObject("SELECT nextval('intervention_ref_seq')", Long.class);
            return n == null ? 1L : n;
        } catch (Exception ex) {
            Long n = jdbc.queryForObject("SELECT NEXT VALUE FOR intervention_ref_seq", Long.class);
            return n == null ? 1L : n;
        }
    }

    @Override
    public List<Intervention> parPointEau(UUID pointEauId) {
        return interventions.findByPointEauId(pointEauId).stream().map(this::versDomaine).toList();
    }

    @Override
    public List<Intervention> enCoursPourComites(Set<UUID> comiteIds) {
        if (comiteIds.isEmpty()) {
            return List.of();
        }
        return interventions.findActivesParComites(comiteIds).stream().map(this::versDomaine).toList();
    }

    private Intervention versDomaine(InterventionEntity e) {
        Set<UUID> sigs = new HashSet<>();
        liens.findByInterventionId(e.getId()).forEach(l -> sigs.add(l.getSignalementId()));
        List<PieceRemplacee> ps = new ArrayList<>();
        for (PieceRemplaceeEntity p : pieces.findByInterventionId(e.getId())) {
            ps.add(new PieceRemplacee(
                    p.getId(), p.getReferencePiece(), p.getLibelle(), p.getQuantite(), p.getCoutUnitaire()));
        }
        CompteRendu cr = e.getDiagnostic() == null && e.getActions() == null
                ? null
                : new CompteRendu(e.getDiagnostic(), e.getCauseRacine(), e.getActions());
        Intervention i = new Intervention(
                e.getId(),
                e.getReference(),
                e.getPointEauId(),
                TypeIntervention.valueOf(e.getType()),
                OrigineIntervention.valueOf(e.getOrigine()),
                e.getTechnicienId(),
                StatutIntervention.valueOf(e.getStatut()),
                e.getEcheanceSouhaitee(),
                e.getMotifSuspension() == null ? null : MotifSuspension.valueOf(e.getMotifSuspension()),
                e.getMotifAnnulation(),
                cr,
                e.getOuverteLe() == null ? Instant.now() : e.getOuverteLe(),
                e.getAffecteeLe(),
                e.getDemarreeLe(),
                e.getRealiseeLe(),
                e.getClotureeLe(),
                e.getTempsRetablissementMinutes(),
                e.getConfirmeeParId(),
                e.getVersion() == null ? 0 : e.getVersion(),
                sigs,
                ps);
        if (e.getInterventionOrigineId() != null) {
            i.rattacherOrigine(e.getInterventionOrigineId());
        }
        return i;
    }
}
