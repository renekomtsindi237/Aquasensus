package org.aquasensus.charge.domain;

import java.util.UUID;

public record PeriodeSaison(
        UUID id, UUID localiteId, String libelle, int jourDebut, int jourFin, double coefficient, boolean actif) {

    public boolean couvre(int jourAnnee) {
        if (jourDebut <= jourFin) {
            return jourAnnee >= jourDebut && jourAnnee <= jourFin;
        }
        return jourAnnee >= jourDebut || jourAnnee <= jourFin;
    }

    public boolean saisonSeche() {
        return coefficient > 1.0;
    }
}
