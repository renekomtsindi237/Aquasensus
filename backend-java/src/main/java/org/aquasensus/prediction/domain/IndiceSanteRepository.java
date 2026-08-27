package org.aquasensus.prediction.domain;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface IndiceSanteRepository {

    Optional<IndiceSante> dernier(UUID pointEauId);

    Optional<IndiceSante> parOuvrageEtDate(UUID pointEauId, LocalDate date);

    IndiceSante enregistrer(IndiceSante indice);
}
