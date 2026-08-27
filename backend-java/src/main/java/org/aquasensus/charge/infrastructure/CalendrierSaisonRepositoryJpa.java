package org.aquasensus.charge.infrastructure;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.aquasensus.charge.domain.CalendrierSaisonRepository;
import org.aquasensus.charge.domain.PeriodeSaison;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class CalendrierSaisonRepositoryJpa implements CalendrierSaisonRepository {

    private final CalendrierSaisonJpa jpa;

    public CalendrierSaisonRepositoryJpa(CalendrierSaisonJpa jpa) {
        this.jpa = jpa;
    }

    @Override
    public List<PeriodeSaison> toutes() {
        return jpa.findAll().stream().map(this::versDomaine).toList();
    }

    @Override
    public Optional<PeriodeSaison> parId(UUID id) {
        return jpa.findById(id).map(this::versDomaine);
    }

    @Override
    @Transactional
    public PeriodeSaison enregistrer(PeriodeSaison periode) {
        CalendrierSaisonEntity e = jpa.findById(periode.id()).orElseGet(CalendrierSaisonEntity::new);
        e.setId(periode.id());
        e.setLocaliteId(periode.localiteId());
        e.setLibelle(periode.libelle());
        e.setJourDebut(periode.jourDebut());
        e.setJourFin(periode.jourFin());
        e.setCoefficient(BigDecimal.valueOf(periode.coefficient()));
        e.setActif(periode.actif());
        jpa.save(e);
        return periode;
    }

    @Override
    @Transactional
    public void supprimer(UUID id) {
        jpa.findById(id).ifPresent(e -> {
            e.setActif(false);
            jpa.save(e);
        });
    }

    private PeriodeSaison versDomaine(CalendrierSaisonEntity e) {
        return new PeriodeSaison(
                e.getId(),
                e.getLocaliteId(),
                e.getLibelle(),
                e.getJourDebut(),
                e.getJourFin(),
                e.getCoefficient().doubleValue(),
                e.isActif());
    }
}
