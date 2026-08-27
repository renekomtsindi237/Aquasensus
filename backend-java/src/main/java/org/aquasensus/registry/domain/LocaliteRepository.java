package org.aquasensus.registry.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LocaliteRepository {

    Optional<Localite> parId(UUID id);

    Optional<Localite> parCode(String code);

    List<Localite> toutes();

    Localite enregistrer(Localite localite);
}
