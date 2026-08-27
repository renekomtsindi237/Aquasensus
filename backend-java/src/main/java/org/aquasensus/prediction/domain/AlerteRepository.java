package org.aquasensus.prediction.domain;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface AlerteRepository {

    Optional<Alerte> parId(UUID id);

    Optional<Alerte> activeParOuvrageEtRegle(UUID pointEauId, TypeRegleAlerte type);

    List<Alerte> activesPourComites(Set<UUID> comiteIds);

    List<Alerte> toutes();

    Alerte enregistrer(Alerte alerte);
}
