package org.aquasensus.identity.application;

import java.time.Instant;
import java.util.UUID;
import org.aquasensus.identity.domain.Utilisateur;
import org.aquasensus.identity.domain.UtilisateurRepository;
import org.aquasensus.shared.error.CompteVerrouilleException;
import org.aquasensus.shared.error.IdentifiantsInvalidesException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthentificationService {

    private final UtilisateurRepository utilisateurs;
    private final PasswordEncoder passwordEncoder;
    private final EmetteurJetons emetteurJetons;
    private final EnregistrementEchecConnexion echecsConnexion;

    public AuthentificationService(
            UtilisateurRepository utilisateurs,
            PasswordEncoder passwordEncoder,
            EmetteurJetons emetteurJetons,
            EnregistrementEchecConnexion echecsConnexion) {
        this.utilisateurs = utilisateurs;
        this.passwordEncoder = passwordEncoder;
        this.emetteurJetons = emetteurJetons;
        this.echecsConnexion = echecsConnexion;
    }

    @Transactional
    public JetonAuthentification connecter(String identifiant, String motDePasse) {
        Instant maintenant = Instant.now();
        Utilisateur utilisateur = utilisateurs
                .parIdentifiant(identifiant)
                .orElseThrow(IdentifiantsInvalidesException::new);

        utilisateur.garantirAcces(maintenant);

        if (!passwordEncoder.matches(motDePasse, utilisateur.motDePasseHache())) {
            Utilisateur apresEchec = echecsConnexion.enregistrer(identifiant, maintenant);
            if (apresEchec.estVerrouille(maintenant)
                    || apresEchec.statut() == org.aquasensus.identity.domain.StatutCompte.VERROUILLE) {
                throw new CompteVerrouilleException();
            }
            throw new IdentifiantsInvalidesException();
        }

        utilisateur.enregistrerSucces();
        utilisateurs.enregistrer(utilisateur);
        return emetteurJetons.emettre(utilisateur);
    }

    @Transactional
    public JetonAuthentification rafraichir(String jetonRafraichissement) {
        return emetteurJetons.renouveler(jetonRafraichissement);
    }

    @Transactional(readOnly = true)
    public Utilisateur exigence(UUID id) {
        return utilisateurs.parId(id).orElseThrow(IdentifiantsInvalidesException::new);
    }
}
