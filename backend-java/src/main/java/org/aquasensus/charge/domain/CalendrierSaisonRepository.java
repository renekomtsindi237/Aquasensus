package org.aquasensus.charge.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CalendrierSaisonRepository {

    List<PeriodeSaison> toutes();

    Optional<PeriodeSaison> parId(UUID id);

    PeriodeSaison enregistrer(PeriodeSaison periode);

    void supprimer(UUID id);
}
