package org.aquasensus.registry.infrastructure;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.aquasensus.registry.domain.Localite;
import org.aquasensus.registry.domain.LocaliteRepository;
import org.aquasensus.registry.domain.NiveauLocalite;
import org.springframework.stereotype.Repository;

@Repository
public class LocaliteRepositoryJpa implements LocaliteRepository {

    private final LocaliteJpa jpa;

    public LocaliteRepositoryJpa(LocaliteJpa jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<Localite> parId(UUID id) {
        return jpa.findById(id).map(this::versDomaine);
    }

    @Override
    public Optional<Localite> parCode(String code) {
        return jpa.findByCode(code).map(this::versDomaine);
    }

    @Override
    public List<Localite> toutes() {
        return jpa.findAll().stream().map(this::versDomaine).toList();
    }

    @Override
    public Localite enregistrer(Localite localite) {
        LocaliteEntity e = jpa.findById(localite.id()).orElseGet(LocaliteEntity::new);
        e.setId(localite.id());
        e.setCode(localite.code());
        e.setNom(localite.nom());
        e.setNiveau(localite.niveau().name());
        e.setParentId(localite.parentId());
        jpa.save(e);
        return localite;
    }

    private Localite versDomaine(LocaliteEntity e) {
        return new Localite(
                e.getId(), e.getCode(), e.getNom(), NiveauLocalite.valueOf(e.getNiveau()), e.getParentId());
    }
}
