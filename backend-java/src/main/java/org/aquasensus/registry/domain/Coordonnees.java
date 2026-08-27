package org.aquasensus.registry.domain;

import java.math.BigDecimal;
import java.util.Objects;
import org.aquasensus.shared.error.RegleMetierException;

/**
 * Coordonnées WGS84. L'emprise v1 couvre le Cameroun (ISS-011).
 */
public record Coordonnees(BigDecimal latitude, BigDecimal longitude) {

    public Coordonnees {
        Objects.requireNonNull(latitude);
        Objects.requireNonNull(longitude);
    }

    public void garantirEmprise(EmpriseGeographique emprise) {
        if (!emprise.contient(this)) {
            throw new RegleMetierException(
                    "EF-01", "Les coordonnées GPS sont hors de l'emprise géographique paramétrée.");
        }
    }
}
