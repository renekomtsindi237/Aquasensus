package org.aquasensus.registry.domain;

import java.math.BigDecimal;

public record EmpriseGeographique(
        BigDecimal latitudeMin,
        BigDecimal latitudeMax,
        BigDecimal longitudeMin,
        BigDecimal longitudeMax) {

    public boolean contient(Coordonnees c) {
        return c.latitude().compareTo(latitudeMin) >= 0
                && c.latitude().compareTo(latitudeMax) <= 0
                && c.longitude().compareTo(longitudeMin) >= 0
                && c.longitude().compareTo(longitudeMax) <= 0;
    }
}
