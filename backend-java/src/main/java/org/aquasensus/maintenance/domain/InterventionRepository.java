package org.aquasensus.maintenance.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InterventionRepository {

    Optional<Intervention> parId(UUID id);

    Intervention enregistrer(Intervention intervention);

    long prochaineReference();

    List<Intervention> parPointEau(UUID pointEauId);

    List<Intervention> enCoursPourComites(java.util.Set<UUID> comiteIds);
}
