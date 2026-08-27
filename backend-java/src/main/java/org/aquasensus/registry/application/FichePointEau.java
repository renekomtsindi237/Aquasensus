package org.aquasensus.registry.application;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.aquasensus.registry.domain.TypePointEau;

public record FichePointEau(
        String code,
        String nomUsage,
        TypePointEau type,
        BigDecimal latitude,
        BigDecimal longitude,
        UUID localiteId,
        UUID comiteId,
        LocalDate dateMiseEnService,
        BigDecimal profondeurM,
        BigDecimal debitNominalLMin,
        Integer populationDesservie,
        Integer intervalleMaintenanceJours) {}
