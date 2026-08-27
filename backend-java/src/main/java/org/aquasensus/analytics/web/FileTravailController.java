package org.aquasensus.analytics.web;

import org.aquasensus.analytics.application.FileTravailService;
import org.aquasensus.identity.web.UtilisateurCourant;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/work-queue")
public class FileTravailController {

    private final FileTravailService fileTravail;

    public FileTravailController(FileTravailService fileTravail) {
        this.fileTravail = fileTravail;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('DELEGUE','ADMIN')")
    public FileTravailService.File file(@AuthenticationPrincipal UtilisateurCourant courant) {
        return fileTravail.file(courant.id());
    }
}
