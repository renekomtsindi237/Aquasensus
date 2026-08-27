package org.aquasensus.identity.domain;

import java.util.Optional;

public interface UtilisateurRepository {

    Optional<Utilisateur> parIdentifiant(String identifiant);

    Optional<Utilisateur> parId(java.util.UUID id);

    Utilisateur enregistrer(Utilisateur utilisateur);

    java.util.List<Utilisateur> lister();
}
