package org.aquasensus.analytics.application;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.aquasensus.identity.domain.CodeRole;
import org.aquasensus.identity.domain.Utilisateur;
import org.aquasensus.identity.domain.UtilisateurRepository;
import org.aquasensus.maintenance.domain.InterventionRepository;
import org.aquasensus.prediction.domain.AlerteRepository;
import org.aquasensus.reporting.domain.SignalementRepository;
import org.aquasensus.shared.error.RessourceIntrouvableException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FileTravailService {

    private final UtilisateurRepository utilisateurs;
    private final SignalementRepository signalements;
    private final InterventionRepository interventions;
    private final AlerteRepository alertes;

    public FileTravailService(
            UtilisateurRepository utilisateurs,
            SignalementRepository signalements,
            InterventionRepository interventions,
            AlerteRepository alertes) {
        this.utilisateurs = utilisateurs;
        this.signalements = signalements;
        this.interventions = interventions;
        this.alertes = alertes;
    }

    @Transactional(readOnly = true)
    public File file(UUID utilisateurId) {
        Utilisateur u = utilisateurs.parId(utilisateurId).orElseThrow(RessourceIntrouvableException::new);
        Set<UUID> comites = u.comitesPerimetre();
        if (u.possede(CodeRole.ADMIN) && comites.isEmpty()) {
            comites = u.comitesPerimetre();
        }
        var aQualifier = signalements.aQualifierPourComites(comites).stream()
                .map(s -> new ItemSignalement(s.id(), s.reference(), s.categorie().name(), s.priorite()))
                .toList();
        var actives = interventions.enCoursPourComites(comites).stream()
                .map(i -> new ItemIntervention(i.id(), i.reference(), i.statut().name(), i.echeanceSouhaitee()))
                .toList();
        var alertesActives = (u.possede(CodeRole.ADMIN) ? alertes.toutes().stream()
                        .filter(a -> a.statut() == org.aquasensus.prediction.domain.StatutAlerte.ACTIVE)
                        : alertes.activesPourComites(comites).stream())
                .map(a -> new ItemAlerte(a.id(), a.typeRegle().name(), a.niveau().name(), a.explication()))
                .toList();
        return new File(aQualifier, actives, alertesActives);
    }

    public record File(
            List<ItemSignalement> signalementsAQualifier,
            List<ItemIntervention> interventionsActives,
            List<ItemAlerte> alertesActives) {}

    public record ItemSignalement(UUID id, String reference, String categorie, int priorite) {}

    public record ItemIntervention(
            UUID id, String reference, String statut, java.time.LocalDate echeanceSouhaitee) {}

    public record ItemAlerte(UUID id, String typeRegle, String niveau, String explication) {}
}
