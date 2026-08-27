package org.aquasensus.prediction.web;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.aquasensus.identity.web.UtilisateurCourant;
import org.aquasensus.prediction.application.AlerteService;
import org.aquasensus.prediction.domain.Alerte;
import org.aquasensus.prediction.domain.StatutAlerte;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/alerts")
public class AlerteController {

    private final AlerteService alertes;

    public AlerteController(AlerteService alertes) {
        this.alertes = alertes;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('DELEGUE','ADMIN','PARTENAIRE')")
    public List<AlerteReponse> lister(@AuthenticationPrincipal UtilisateurCourant courant) {
        return alertes.actives(courant.id()).stream().map(AlerteReponse::depuis).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('DELEGUE','ADMIN','PARTENAIRE')")
    public AlerteReponse detail(@PathVariable UUID id, @AuthenticationPrincipal UtilisateurCourant courant) {
        return AlerteReponse.depuis(alertes.consulter(id, courant.id()));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('DELEGUE','ADMIN')")
    public AlerteReponse patch(
            @PathVariable UUID id,
            @RequestBody PatchAlerte corps,
            @AuthenticationPrincipal UtilisateurCourant courant) {
        return AlerteReponse.depuis(
                alertes.transiter(id, corps.statut(), corps.motif(), corps.reporterJusqua(), courant.id()));
    }

    public record PatchAlerte(StatutAlerte statut, String motif, LocalDate reporterJusqua) {}

    public record AlerteReponse(
            UUID id,
            UUID pointEauId,
            String typeRegle,
            String niveau,
            int horizonJours,
            String explication,
            String recommandation,
            String facteurs,
            String statut,
            String motifContestation,
            String issue,
            String versionParametrage) {

        static AlerteReponse depuis(Alerte a) {
            return new AlerteReponse(
                    a.id(),
                    a.pointEauId(),
                    a.typeRegle().name(),
                    a.niveau().name(),
                    a.horizonJours(),
                    a.explication(),
                    a.recommandation(),
                    a.facteurs(),
                    a.statut().name(),
                    a.motifContestation(),
                    a.issue() == null ? null : a.issue().name(),
                    a.versionParametrage());
        }
    }
}
