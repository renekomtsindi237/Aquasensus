package org.aquasensus.registry.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PointEauRepository {

    Optional<PointEau> parId(UUID id);

    Optional<PointEau> parCode(String code);

    PointEau enregistrer(PointEau pointEau);

    List<PointEau> rechercher(FiltrePointEau filtre);

    List<PointEau> actifs();
}
