package org.aquasensus.maintenance.web;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import org.aquasensus.identity.web.UtilisateurCourant;
import org.aquasensus.maintenance.application.InterventionService;
import org.aquasensus.maintenance.domain.CompteRendu;
import org.aquasensus.maintenance.domain.Intervention;
import org.aquasensus.maintenance.domain.MotifSuspension;
import org.aquasensus.maintenance.domain.OrigineIntervention;
import org.aquasensus.maintenance.domain.PieceRemplacee;
import org.aquasensus.maintenance.domain.StatutIntervention;
import org.aquasensus.maintenance.domain.TypeIntervention;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/interventions")
public class InterventionController {

    private final InterventionService service;

    public InterventionController(InterventionService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('DELEGUE','ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public InterventionReponse ouvrir(
            @RequestBody OuvertureRequete requete, @AuthenticationPrincipal UtilisateurCourant courant) {
        return InterventionReponse.depuis(service.ouvrir(
                new InterventionService.CommandeOuverture(
                        requete.pointEauId(), requete.type(), requete.origine(), requete.signalementIds()),
                courant.id()));
    }

    @PostMapping("/{id}/affectation")
    @PreAuthorize("hasAnyRole('DELEGUE','ADMIN')")
    public InterventionReponse affecter(
            @PathVariable UUID id,
            @RequestBody AffectationRequete requete,
            @AuthenticationPrincipal UtilisateurCourant courant) {
        return InterventionReponse.depuis(service.affecter(
                id, requete.technicienId(), requete.echeanceSouhaitee(), requete.version(), courant.id()));
    }

    @PostMapping("/{id}/transitions")
    public InterventionReponse transiter(
            @PathVariable UUID id,
            @RequestBody TransitionRequete requete,
            @AuthenticationPrincipal UtilisateurCourant courant) {
        CompteRendu cr = requete.diagnostic() == null
                ? null
                : new CompteRendu(requete.diagnostic(), requete.causeRacine(), requete.actions());
        return InterventionReponse.depuis(service.transiter(
                id,
                new InterventionService.CommandeTransition(
                        requete.cible(),
                        requete.version(),
                        requete.motifSuspension(),
                        requete.motifAnnulation(),
                        cr),
                courant.id()));
    }

    @PutMapping("/{id}/report")
    public InterventionReponse rapport(
            @PathVariable UUID id,
            @RequestBody CompteRendu rapport,
            @AuthenticationPrincipal UtilisateurCourant courant) {
        return InterventionReponse.depuis(service.enregistrerCompteRendu(id, rapport, courant.id()));
    }

    @PostMapping("/{id}/parts")
    public InterventionReponse piece(
            @PathVariable UUID id,
            @RequestBody PieceRequete requete,
            @AuthenticationPrincipal UtilisateurCourant courant) {
        PieceRemplacee piece = new PieceRemplacee(
                UUID.randomUUID(),
                requete.reference(),
                requete.libelle(),
                requete.quantite(),
                requete.coutUnitaire());
        return InterventionReponse.depuis(service.ajouterPiece(id, piece, courant.id()));
    }

    @GetMapping("/{id}")
    public InterventionReponse detail(@PathVariable UUID id) {
        return InterventionReponse.depuis(service.consulter(id));
    }

    @PostMapping("/{id}/reouverture")
    @PreAuthorize("hasAnyRole('DELEGUE','ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public InterventionReponse reouvrir(
            @PathVariable UUID id, @AuthenticationPrincipal UtilisateurCourant courant) {
        return InterventionReponse.depuis(service.reouvrir(id, courant.id()));
    }

    @GetMapping("/{id}/briefing")
    public InterventionService.DossierBriefing briefing(
            @PathVariable UUID id, @AuthenticationPrincipal UtilisateurCourant courant) {
        return service.briefing(id, courant.id());
    }

    public record OuvertureRequete(
            UUID pointEauId,
            TypeIntervention type,
            OrigineIntervention origine,
            Set<UUID> signalementIds) {}

    public record AffectationRequete(UUID technicienId, LocalDate echeanceSouhaitee, int version) {}

    public record TransitionRequete(
            StatutIntervention cible,
            int version,
            MotifSuspension motifSuspension,
            String motifAnnulation,
            String diagnostic,
            String causeRacine,
            String actions) {}

    public record PieceRequete(
            String reference, String libelle, int quantite, java.math.BigDecimal coutUnitaire) {}

    public record InterventionReponse(
            UUID id,
            String reference,
            StatutIntervention statut,
            int version,
            UUID technicienId,
            Integer tempsRetablissementMinutes,
            java.time.Instant affecteeLe,
            java.time.Instant clotureeLe,
            UUID interventionOrigineId) {

        static InterventionReponse depuis(Intervention i) {
            return new InterventionReponse(
                    i.id(),
                    i.reference(),
                    i.statut(),
                    i.version(),
                    i.technicienId(),
                    i.tempsRetablissementMinutes(),
                    i.affecteeLe(),
                    i.clotureeLe(),
                    i.interventionOrigineId());
        }
    }
}
