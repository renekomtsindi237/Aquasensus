package org.aquasensus.notify.application;

import org.aquasensus.identity.domain.UtilisateurRepository;
import org.aquasensus.maintenance.application.InterventionService;
import org.aquasensus.prediction.application.PublicationAnalyticsService;
import org.aquasensus.registry.domain.PointEau;
import org.aquasensus.registry.domain.PointEauRepository;
import org.aquasensus.reporting.application.SignalementService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class NotificationApresCommit {

    private final NotificationService notifications;
    private final UtilisateurRepository utilisateurs;
    private final PointEauRepository points;

    public NotificationApresCommit(
            NotificationService notifications,
            UtilisateurRepository utilisateurs,
            PointEauRepository points) {
        this.notifications = notifications;
        this.utilisateurs = utilisateurs;
        this.points = points;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void affectation(InterventionService.TechnicienAffecte event) {
        String corps = "Intervention affectee. AquaSensus, aucun volume d'eau.";
        notifications.notifier(
                event.technicienId(),
                "IN_APP",
                "INTERVENTION_AFFECTEE",
                "Intervention affectée",
                corps,
                "INTERVENTION_AFFECTEE:" + event.interventionId() + ":" + event.technicienId());
        notifications.notifier(
                event.technicienId(),
                "SMS",
                "INTERVENTION_AFFECTEE",
                "Intervention affectée",
                corps,
                "INTERVENTION_AFFECTEE:SMS:" + event.interventionId());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void retablissement(InterventionService.RetablissementConfirme event) {
        PointEau o = points.parId(event.pointEauId()).orElse(null);
        if (o == null) {
            return;
        }
        notifications.auxDeleguesDuComite(
                o.comiteId(),
                "RETABLISSEMENT_CONFIRME",
                "Rétablissement confirmé",
                "Ouvrage retabli. AquaSensus, aucun volume d'eau.",
                event.interventionId().toString());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void signalementGrave(SignalementService.SignalementGrave event) {
        notifications.auxDeleguesDuComite(
                event.comiteId(),
                "SIGNALEMENT_GRAVE",
                "Signalement grave",
                "Un signalement de gravite haute a ete recu. Aucun volume d'eau.",
                event.signalementId().toString());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void alerte(PublicationAnalyticsService.AlerteEmise event) {
        PointEau o = points.parId(event.pointEauId()).orElse(null);
        if (o == null) {
            return;
        }
        notifications.auxDeleguesDuComite(
                o.comiteId(),
                "ALERTE_EMISE",
                "Alerte d'usure",
                "Alerte interpretable emise. Aucun volume d'eau.",
                event.alerteId().toString());
    }
}
