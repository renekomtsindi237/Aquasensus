package org.aquasensus.prediction.infrastructure;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.aquasensus.prediction.domain.Alerte;
import org.aquasensus.prediction.domain.AlerteRepository;
import org.aquasensus.prediction.domain.IssueAlerte;
import org.aquasensus.prediction.domain.NiveauAlerte;
import org.aquasensus.prediction.domain.StatutAlerte;
import org.aquasensus.prediction.domain.TypeRegleAlerte;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class AlerteRepositoryJpa implements AlerteRepository {

    private final AlerteJpa jpa;

    public AlerteRepositoryJpa(AlerteJpa jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<Alerte> parId(UUID id) {
        return jpa.findById(id).map(this::versDomaine);
    }

    @Override
    public Optional<Alerte> activeParOuvrageEtRegle(UUID pointEauId, TypeRegleAlerte type) {
        return jpa.findByPointEauIdAndTypeRegleAndStatut(pointEauId, type.name(), StatutAlerte.ACTIVE.name())
                .map(this::versDomaine);
    }

    @Override
    public List<Alerte> activesPourComites(Set<UUID> comiteIds) {
        if (comiteIds.isEmpty()) {
            return List.of();
        }
        return jpa.findActivesParComites(comiteIds).stream().map(this::versDomaine).toList();
    }

    @Override
    public List<Alerte> toutes() {
        return jpa.findAll().stream().map(this::versDomaine).toList();
    }

    @Override
    @Transactional
    public Alerte enregistrer(Alerte alerte) {
        AlerteEntity e = jpa.findById(alerte.id()).orElseGet(AlerteEntity::new);
        e.setId(alerte.id());
        e.setPointEauId(alerte.pointEauId());
        e.setTypeRegle(alerte.typeRegle().name());
        e.setNiveau(alerte.niveau().name());
        e.setHorizonJours(alerte.horizonJours());
        e.setEmiseLe(alerte.emiseLe());
        e.setExplication(alerte.explication());
        e.setRecommandation(alerte.recommandation());
        e.setFacteurs(alerte.facteurs());
        e.setStatut(alerte.statut().name());
        e.setMotifContestation(alerte.motifContestation());
        e.setReporterJusqua(alerte.reporterJusqua());
        e.setIssue(alerte.issue() == null ? null : alerte.issue().name());
        e.setVersionParametrage(alerte.versionParametrage());
        jpa.save(e);
        return alerte;
    }

    private Alerte versDomaine(AlerteEntity e) {
        return new Alerte(
                e.getId(),
                e.getPointEauId(),
                TypeRegleAlerte.valueOf(e.getTypeRegle()),
                NiveauAlerte.valueOf(e.getNiveau()),
                e.getHorizonJours(),
                e.getEmiseLe(),
                e.getExplication(),
                e.getRecommandation(),
                e.getFacteurs(),
                StatutAlerte.valueOf(e.getStatut()),
                e.getMotifContestation(),
                e.getReporterJusqua(),
                e.getIssue() == null ? null : IssueAlerte.valueOf(e.getIssue()),
                e.getVersionParametrage());
    }
}
