package org.aquasensus.maintenance.domain;

import java.util.EnumSet;
import java.util.Set;
import org.aquasensus.shared.error.RegleMetierException;

public enum StatutIntervention {
    OUVERTE,
    AFFECTEE,
    EN_COURS,
    SUSPENDUE,
    REALISEE,
    CLOTUREE,
    ANNULEE;

    public void garantirVers(StatutIntervention cible) {
        if (!transitions().contains(cible)) {
            throw new RegleMetierException(
                    "EF-22", "La transition " + this + " → " + cible + " est interdite.");
        }
    }

    public Set<StatutIntervention> transitions() {
        return switch (this) {
            case OUVERTE -> EnumSet.of(AFFECTEE, ANNULEE);
            case AFFECTEE -> EnumSet.of(EN_COURS, ANNULEE);
            case EN_COURS -> EnumSet.of(SUSPENDUE, REALISEE);
            case SUSPENDUE -> EnumSet.of(EN_COURS, ANNULEE);
            case REALISEE -> EnumSet.of(EN_COURS, CLOTUREE);
            case CLOTUREE, ANNULEE -> EnumSet.noneOf(StatutIntervention.class);
        };
    }
}
