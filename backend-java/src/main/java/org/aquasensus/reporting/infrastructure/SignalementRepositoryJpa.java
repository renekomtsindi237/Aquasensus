package org.aquasensus.reporting.infrastructure;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.aquasensus.reporting.domain.CanalSignalement;
import org.aquasensus.reporting.domain.CategorieSymptome;
import org.aquasensus.reporting.domain.Gravite;
import org.aquasensus.reporting.domain.Signalement;
import org.aquasensus.reporting.domain.SignalementRepository;
import org.aquasensus.reporting.domain.StatutSignalement;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class SignalementRepositoryJpa implements SignalementRepository {

    private static final List<String> INCIDENTS_OUVERTS =
            List.of(StatutSignalement.RECU.name(), StatutSignalement.QUALIFIE.name());

    private final SignalementJpa jpa;
    private final JdbcTemplate jdbc;

    public SignalementRepositoryJpa(SignalementJpa jpa, JdbcTemplate jdbc) {
        this.jpa = jpa;
        this.jdbc = jdbc;
    }

    @Override
    public Optional<Signalement> parId(UUID id) {
        return jpa.findById(id).map(this::versDomaine);
    }

    @Override
    public Optional<Signalement> parUuidClient(UUID uuidClient) {
        return jpa.findByUuidClient(uuidClient).map(this::versDomaine);
    }

    @Override
    public Optional<Signalement> incidentOuvert(UUID pointEauId, CategorieSymptome categorie, Instant depuis) {
        return jpa.findFirstByPointEauIdAndCategorieAndStatutInAndDeclareLeGreaterThanEqualOrderByDeclareLeAsc(
                        pointEauId, categorie.name(), INCIDENTS_OUVERTS, depuis)
                .map(this::versDomaine);
    }

    @Override
    public List<Signalement> recentsParTelephoneHache(String hache, Instant depuis) {
        return jpa.findByDeclarantTelephoneHacheAndDeclareLeGreaterThanEqual(hache, depuis).stream()
                .map(this::versDomaine)
                .toList();
    }

    @Override
    public List<Signalement> parIds(java.util.Collection<UUID> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }
        return jpa.findAllById(ids).stream().map(this::versDomaine).toList();
    }

    @Override
    public List<Signalement> aQualifierPourComites(java.util.Collection<UUID> comiteIds) {
        if (comiteIds.isEmpty()) {
            return List.of();
        }
        return jpa.findAQualifier(comiteIds).stream().map(this::versDomaine).toList();
    }

    @Override
    public List<Signalement> parPointEau(UUID pointEauId) {
        return jpa.findByPointEauId(pointEauId).stream().map(this::versDomaine).toList();
    }

    @Override
    @Transactional
    public Signalement enregistrer(Signalement s) {
        SignalementEntity e = jpa.findById(s.id()).orElseGet(SignalementEntity::new);
        e.setId(s.id());
        e.setReference(s.reference());
        e.setUuidClient(s.uuidClient());
        e.setPointEauId(s.pointEauId());
        e.setCategorie(s.categorie().name());
        e.setGravite(s.gravite().name());
        e.setCommentaire(s.commentaire());
        e.setDeclarantUtilisateurId(s.declarantUtilisateurId());
        e.setDeclarantTelephoneHache(s.telephoneHache());
        e.setDeclarantTelephoneSuffixe(s.telephoneSuffixe());
        e.setCanal(s.canal().name());
        e.setStatut(s.statut().name());
        e.setSignalementParentId(s.signalementParentId());
        e.setNbCorroborations(s.nbCorroborations());
        e.setPriorite(s.priorite());
        e.setPrioriteFigee(s.prioriteFigee());
        e.setMotifQualification(s.motifQualification());
        e.setDeclareLe(s.declareLe());
        jpa.save(e);
        return s;
    }

    @Override
    public long prochaineReference() {
        try {
            Long n = jdbc.queryForObject("SELECT nextval('signalement_ref_seq')", Long.class);
            return n == null ? 1L : n;
        } catch (Exception ex) {
            Long n = jdbc.queryForObject("SELECT NEXT VALUE FOR signalement_ref_seq", Long.class);
            return n == null ? 1L : n;
        }
    }

    private Signalement versDomaine(SignalementEntity e) {
        return new Signalement(
                e.getId(),
                e.getReference(),
                e.getUuidClient(),
                e.getPointEauId(),
                CategorieSymptome.valueOf(e.getCategorie()),
                Gravite.valueOf(e.getGravite()),
                e.getCommentaire(),
                e.getDeclarantUtilisateurId(),
                e.getDeclarantTelephoneHache(),
                e.getDeclarantTelephoneSuffixe(),
                CanalSignalement.valueOf(e.getCanal()),
                StatutSignalement.valueOf(e.getStatut()),
                e.getSignalementParentId(),
                e.getNbCorroborations(),
                e.getPriorite(),
                e.isPrioriteFigee(),
                e.getMotifQualification(),
                e.getDeclareLe());
    }
}
