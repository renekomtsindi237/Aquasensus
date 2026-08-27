package org.aquasensus.reporting.web;

import java.time.Instant;
import java.util.UUID;
import org.aquasensus.identity.web.UtilisateurCourant;
import org.aquasensus.registry.domain.PointEau;
import org.aquasensus.reporting.application.SignalementService;
import org.aquasensus.reporting.domain.CanalSignalement;
import org.aquasensus.reporting.domain.CategorieSymptome;
import org.aquasensus.reporting.domain.Gravite;
import org.aquasensus.reporting.domain.Signalement;
import org.aquasensus.reporting.domain.StatutSignalement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reports")
public class SignalementController {

    private final SignalementService service;

    public SignalementController(SignalementService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<SignalementReponse> creer(
            @RequestHeader("X-Client-Request-Id") UUID uuidClient,
            @RequestBody SignalementRequete requete,
            @AuthenticationPrincipal UtilisateurCourant courant) {
        var cmd = new SignalementService.CommandeSignalement(
                uuidClient,
                requete.pointEauCode(),
                requete.categorie(),
                requete.gravite(),
                requete.commentaire(),
                requete.canal(),
                requete.declarantTelephone(),
                requete.codeOtp(),
                requete.declareLe(),
                courant == null ? null : courant.id());
        var resultat = service.declarer(cmd);
        HttpStatus statut = resultat.rejoue() ? HttpStatus.OK : HttpStatus.CREATED;
        return ResponseEntity.status(statut).body(SignalementReponse.depuis(resultat));
    }

    @PatchMapping("/{id}/qualification")
    @PreAuthorize("hasAnyRole('DELEGUE','ADMIN')")
    public QualificationReponse qualifier(
            @PathVariable UUID id,
            @RequestBody QualificationRequete requete,
            @AuthenticationPrincipal UtilisateurCourant courant) {
        Signalement s = service.qualifier(id, courant.id(), requete.decision(), requete.motif());
        return new QualificationReponse(s.id(), s.reference(), s.statut(), s.motifQualification());
    }

    @PatchMapping("/{id}/priorite")
    @PreAuthorize("hasAnyRole('DELEGUE','ADMIN')")
    public PrioriteReponse priorite(
            @PathVariable UUID id,
            @RequestBody PrioriteRequete requete,
            @AuthenticationPrincipal UtilisateurCourant courant) {
        Signalement s = service.figerPriorite(id, courant.id(), requete.priorite(), requete.motif());
        return new PrioriteReponse(s.id(), s.priorite(), s.prioriteFigee());
    }

    public record SignalementRequete(
            String pointEauCode,
            CategorieSymptome categorie,
            Gravite gravite,
            String commentaire,
            CanalSignalement canal,
            String declarantTelephone,
            String codeOtp,
            Instant declareLe) {}

    public record QualificationRequete(StatutSignalement decision, String motif) {}

    public record PrioriteRequete(int priorite, String motif) {}

    public record PrioriteReponse(UUID id, int priorite, boolean prioriteFigee) {}

    public record QualificationReponse(
            UUID id, String reference, StatutSignalement statut, String motif) {}

    public record SignalementReponse(
            UUID id,
            String reference,
            StatutSignalement statut,
            int nbCorroborations,
            PointEauMini pointEau,
            PriseEnCharge priseEnCharge) {

        static SignalementReponse depuis(SignalementService.ResultatSignalement r) {
            Signalement incident = r.incident();
            PointEau o = r.ouvrage();
            boolean deja = incident.nbCorroborations() > 0;
            String message;
            if (deja) {
                message = "Déjà signalé par " + (incident.nbCorroborations() + 1)
                        + " personne(s). Le comité a été averti.";
            } else {
                message = "Signalement reçu. Le comité va en prendre connaissance.";
            }
            return new SignalementReponse(
                    r.enregistre().id(),
                    r.enregistre().reference(),
                    r.enregistre().statut(),
                    incident.nbCorroborations(),
                    new PointEauMini(o.code(), o.nomUsage(), o.etat().name()),
                    new PriseEnCharge(
                            deja,
                            o.etat() == org.aquasensus.registry.domain.EtatPointEau.EN_REPARATION,
                            message));
        }
    }

    public record PointEauMini(String code, String nomUsage, String etat) {}

    public record PriseEnCharge(boolean dejaSignale, boolean interventionEnCours, String message) {}
}
