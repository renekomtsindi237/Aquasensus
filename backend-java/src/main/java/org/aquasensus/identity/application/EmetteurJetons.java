package org.aquasensus.identity.application;

import org.aquasensus.identity.domain.Utilisateur;

public interface EmetteurJetons {

    JetonAuthentification emettre(Utilisateur utilisateur);

    JetonAuthentification renouveler(String jetonRafraichissementBrut);
}
