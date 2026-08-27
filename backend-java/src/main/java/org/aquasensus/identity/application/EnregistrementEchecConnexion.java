package org.aquasensus.identity.application;

import java.time.Instant;
import org.aquasensus.identity.domain.Utilisateur;
import org.aquasensus.identity.domain.UtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EnregistrementEchecConnexion {

    private final UtilisateurRepository utilisateurs;

    public EnregistrementEchecConnexion(UtilisateurRepository utilisateurs) {
        this.utilisateurs = utilisateurs;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Utilisateur enregistrer(String identifiant, Instant maintenant) {
        Utilisateur utilisateur = utilisateurs.parIdentifiant(identifiant).orElseThrow();
        utilisateur.enregistrerEchec(maintenant);
        return utilisateurs.enregistrer(utilisateur);
    }
}
