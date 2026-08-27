package org.aquasensus.identity.web;

import jakarta.validation.Valid;
import org.aquasensus.identity.application.AuthentificationService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthentificationService authentification;
    private final org.aquasensus.identity.application.ReinitMotDePasseService reinit;
    private final org.aquasensus.identity.application.CompteService comptes;

    public AuthController(
            AuthentificationService authentification,
            org.aquasensus.identity.application.ReinitMotDePasseService reinit,
            org.aquasensus.identity.application.CompteService comptes) {
        this.authentification = authentification;
        this.reinit = reinit;
        this.comptes = comptes;
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public AuthReponse login(@Valid @RequestBody LoginRequete requete) {
        return AuthReponse.depuis(authentification.connecter(requete.identifiant(), requete.motDePasse()));
    }

    @PostMapping("/refresh")
    public AuthReponse rafraichir(@Valid @RequestBody RafraichirRequete requete) {
        return AuthReponse.depuis(authentification.rafraichir(requete.jetonRafraichissement()));
    }

    @PostMapping("/password/reset-request")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public java.util.Map<String, String> demanderReset(@RequestBody ResetDemande corps) {
        reinit.demander(corps.identifiant());
        return java.util.Map.of("message", org.aquasensus.identity.application.ReinitMotDePasseService.MESSAGE_UNIVOQUE);
    }

    @PostMapping("/password/reset")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void confirmerReset(@RequestBody ResetConfirmation corps) {
        reinit.confirmer(corps.identifiant(), corps.code(), corps.nouveauMotDePasse());
    }

    @PostMapping("/password/change")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changer(
            @AuthenticationPrincipal UtilisateurCourant courant, @RequestBody ChangementMotDePasse corps) {
        comptes.changerMotDePasse(courant.id(), corps.actuel(), corps.nouveau());
    }

    public record ResetDemande(String identifiant) {}

    public record ResetConfirmation(String identifiant, String code, String nouveauMotDePasse) {}

    public record ChangementMotDePasse(String actuel, String nouveau) {}
}
