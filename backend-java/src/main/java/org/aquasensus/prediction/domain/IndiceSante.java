package org.aquasensus.prediction.domain;

import java.time.LocalDate;
import java.util.UUID;

public record IndiceSante(
        UUID id,
        UUID pointEauId,
        LocalDate dateCalcul,
        double score,
        String bande,
        String confiance,
        Double chargeCumuleeJours,
        Integer intervalleEffectifJours,
        Double indicateurM,
        Double indicateurP,
        Double indicateurS,
        Double indicateurT,
        String facteurs,
        String versionParametrage) {}
