package org.aquasensus.prediction.web;

import java.util.List;
import org.aquasensus.identity.web.UtilisateurCourant;
import org.aquasensus.prediction.application.ParametrageMoteurService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/engine/parameters")
public class ParametrageMoteurController {

    private final ParametrageMoteurService parametrage;

    public ParametrageMoteurController(ParametrageMoteurService parametrage) {
        this.parametrage = parametrage;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','PARTENAIRE')")
    public ParametrageMoteurService.Version actif() {
        return parametrage.actif();
    }

    @GetMapping("/history")
    @PreAuthorize("hasRole('ADMIN')")
    public List<ParametrageMoteurService.Version> historique() {
        return parametrage.historique();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ParametrageMoteurService.Version publier(
            @RequestBody VersionRequete req, @AuthenticationPrincipal UtilisateurCourant courant) {
        return parametrage.publier(req.version(), req.contenu(), courant.id());
    }

    public record VersionRequete(String version, String contenu) {}
}
