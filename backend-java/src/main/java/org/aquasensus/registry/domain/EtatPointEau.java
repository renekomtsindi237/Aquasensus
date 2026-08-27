package org.aquasensus.registry.domain;

import java.util.EnumSet;
import java.util.Set;
import org.aquasensus.shared.error.RegleMetierException;

public enum EtatPointEau {
    OPERATIONNEL,
    SOUS_SURVEILLANCE,
    RISQUE_ELEVE,
    EN_PANNE,
    EN_REPARATION,
    HORS_SERVICE;

    public String libelle() {
        return switch (this) {
            case OPERATIONNEL -> "Opérationnel";
            case SOUS_SURVEILLANCE -> "Sous surveillance";
            case RISQUE_ELEVE -> "Risque élevé";
            case EN_PANNE -> "En panne";
            case EN_REPARATION -> "En réparation";
            case HORS_SERVICE -> "Hors service";
        };
    }

    public String formeMarqueur() {
        return switch (this) {
            case OPERATIONNEL -> "cercle-plein";
            case SOUS_SURVEILLANCE -> "cercle-anneau";
            case RISQUE_ELEVE -> "triangle";
            case EN_PANNE -> "losange";
            case EN_REPARATION -> "losange-lisere";
            case HORS_SERVICE -> "cercle-barre";
        };
    }
    public boolean autoriseVers(EtatPointEau cible) {
        if (this == cible) {
            return true;
        }
        return transitions().contains(cible);
    }

    public void garantirTransition(EtatPointEau cible) {
        if (!autoriseVers(cible)) {
            throw new RegleMetierException(
                    "EF-04", "Transition " + this + " → " + cible + " interdite.");
        }
    }

    private Set<EtatPointEau> transitions() {
        return switch (this) {
            case OPERATIONNEL -> EnumSet.of(SOUS_SURVEILLANCE, EN_PANNE);
            case SOUS_SURVEILLANCE -> EnumSet.of(OPERATIONNEL, RISQUE_ELEVE, EN_PANNE);
            case RISQUE_ELEVE -> EnumSet.of(SOUS_SURVEILLANCE, EN_PANNE);
            case EN_PANNE -> EnumSet.of(EN_REPARATION);
            case EN_REPARATION -> EnumSet.of(OPERATIONNEL, EN_PANNE, HORS_SERVICE);
            case HORS_SERVICE -> EnumSet.of(OPERATIONNEL);
        };
    }
}
