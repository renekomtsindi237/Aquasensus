package org.aquasensus.registry.web;

import java.util.UUID;
import org.aquasensus.identity.web.UtilisateurCourant;
import org.aquasensus.registry.application.ConsultationComiteService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/comites")
public class ComiteController {

    private final ConsultationComiteService consultation;

    public ComiteController(ConsultationComiteService consultation) {
        this.consultation = consultation;
    }

    @GetMapping("/{id}")
    public ConsultationComiteService.ComiteResume consulter(
            @PathVariable UUID id, @AuthenticationPrincipal UtilisateurCourant courant) {
        return consultation.consulter(courant.id(), id);
    }
}
