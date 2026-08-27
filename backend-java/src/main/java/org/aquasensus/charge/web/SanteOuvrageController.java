package org.aquasensus.charge.web;

import java.time.LocalDate;
import java.util.UUID;
import org.aquasensus.charge.application.SanteOuvrageService;
import org.aquasensus.charge.domain.ResultatCharge;
import org.aquasensus.identity.web.UtilisateurCourant;
import org.aquasensus.prediction.domain.IndiceSante;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/water-points")
public class SanteOuvrageController {

    private final SanteOuvrageService sante;

    public SanteOuvrageController(SanteOuvrageService sante) {
        this.sante = sante;
    }

    @GetMapping("/{id:[0-9a-fA-F-]{36}}/health")
    @PreAuthorize("hasAnyRole('DELEGUE','ADMIN','PARTENAIRE')")
    public SanteReponse sante(@PathVariable UUID id, @AuthenticationPrincipal UtilisateurCourant courant) {
        return SanteReponse.depuis(sante.dossier(id, courant.id()));
    }

    public record SanteReponse(
            UUID pointEauId,
            String code,
            Double chargeCumuleeJours,
            Integer intervalleEffectifJours,
            Double m,
            LocalDate echeanceMaintenance,
            String unite,
            String explication,
            boolean referentielIncomplet,
            String invitationCorrection,
            Double score,
            String bande,
            String confiance) {

        static SanteReponse depuis(SanteOuvrageService.DossierSante d) {
            ResultatCharge c = d.charge();
            IndiceSante i = d.dernierIndice();
            return new SanteReponse(
                    d.pointEauId(),
                    d.code(),
                    c.chargeCumuleeJours(),
                    c.intervalleEffectifJours(),
                    c.m(),
                    d.echeanceMaintenance(),
                    "jours pondérés",
                    c.explication(),
                    c.referentielIncomplet(),
                    c.invitationCorrection(),
                    i == null ? null : i.score(),
                    i == null ? null : i.bande(),
                    i == null ? null : i.confiance());
        }
    }
}
