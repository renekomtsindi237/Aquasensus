package org.aquasensus.prediction.application;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.aquasensus.prediction.domain.Alerte;
import org.aquasensus.prediction.domain.AlerteRepository;
import org.aquasensus.prediction.domain.IndiceSante;
import org.aquasensus.prediction.domain.IndiceSanteRepository;
import org.aquasensus.prediction.domain.NiveauAlerte;
import org.aquasensus.prediction.domain.TypeRegleAlerte;
import org.aquasensus.registry.domain.EtatPointEau;
import org.aquasensus.registry.domain.PointEau;
import org.aquasensus.registry.domain.PointEauRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PublicationAnalyticsService {

    private final IndiceSanteRepository indices;
    private final AlerteRepository alertes;
    private final PointEauRepository points;
    private final ApplicationEventPublisher events;

    public PublicationAnalyticsService(
            IndiceSanteRepository indices,
            AlerteRepository alertes,
            PointEauRepository points,
            ApplicationEventPublisher events) {
        this.indices = indices;
        this.alertes = alertes;
        this.points = points;
        this.events = events;
    }

    @Transactional
    public int publierIndices(List<IndicePublie> lots) {
        int n = 0;
        for (IndicePublie i : lots) {
            indices.enregistrer(new IndiceSante(
                    UUID.randomUUID(),
                    i.pointEauId(),
                    i.dateCalcul(),
                    i.score(),
                    i.bande(),
                    i.confiance(),
                    i.chargeCumuleeJours(),
                    i.intervalleEffectifJours(),
                    i.indicateurM(),
                    i.indicateurP(),
                    i.indicateurS(),
                    i.indicateurT(),
                    i.facteurs() == null ? "[]" : i.facteurs(),
                    i.versionParametrage()));
            appliquerBande(i.pointEauId(), i.bande(), i.confiance());
            n++;
        }
        return n;
    }

    @Transactional
    public int publierAlertes(List<AlertePubliee> lots) {
        int n = 0;
        for (AlertePubliee a : lots) {
            TypeRegleAlerte type = TypeRegleAlerte.valueOf(a.typeRegle());
            if (alertes.activeParOuvrageEtRegle(a.pointEauId(), type).isPresent()) {
                continue;
            }
            Alerte emise = Alerte.emettre(
                    a.pointEauId(),
                    type,
                    NiveauAlerte.valueOf(a.niveau()),
                    a.horizonJours(),
                    a.explication(),
                    a.recommandation() == null ? "Planifier une inspection." : a.recommandation(),
                    a.facteurs() == null ? "[]" : a.facteurs(),
                    a.versionParametrage());
            alertes.enregistrer(emise);
            events.publishEvent(new AlerteEmise(emise.id(), emise.pointEauId()));
            n++;
        }
        return n;
    }

    private void appliquerBande(UUID pointEauId, String bande, String confiance) {
        PointEau ouvrage = points.parId(pointEauId).orElse(null);
        if (ouvrage == null) {
            return;
        }
        if (ouvrage.etat() == EtatPointEau.EN_PANNE
                || ouvrage.etat() == EtatPointEau.EN_REPARATION
                || ouvrage.etat() == EtatPointEau.HORS_SERVICE) {
            return;
        }
        EtatPointEau cible = switch (bande) {
            case "SOUS_SURVEILLANCE" -> EtatPointEau.SOUS_SURVEILLANCE;
            case "RISQUE_ELEVE", "CRITIQUE" -> EtatPointEau.RISQUE_ELEVE;
            default -> EtatPointEau.OPERATIONNEL;
        };
        while (ouvrage.etat() != cible && ouvrage.etat().autoriseVers(etapeVers(ouvrage.etat(), cible))) {
            EtatPointEau suivant = etapeVers(ouvrage.etat(), cible);
            ouvrage.changerEtat(suivant, "Indice de santé " + bande + " (confiance " + confiance + ")", null);
        }
        points.enregistrer(ouvrage);
    }

    private static EtatPointEau etapeVers(EtatPointEau actuel, EtatPointEau cible) {
        if (actuel == cible) {
            return actuel;
        }
        if (actuel == EtatPointEau.OPERATIONNEL && cible != EtatPointEau.OPERATIONNEL) {
            return EtatPointEau.SOUS_SURVEILLANCE;
        }
        if (actuel == EtatPointEau.SOUS_SURVEILLANCE && cible == EtatPointEau.RISQUE_ELEVE) {
            return EtatPointEau.RISQUE_ELEVE;
        }
        if (actuel == EtatPointEau.SOUS_SURVEILLANCE && cible == EtatPointEau.OPERATIONNEL) {
            return EtatPointEau.OPERATIONNEL;
        }
        if (actuel == EtatPointEau.RISQUE_ELEVE && cible != EtatPointEau.RISQUE_ELEVE) {
            return EtatPointEau.SOUS_SURVEILLANCE;
        }
        return actuel;
    }

    public record IndicePublie(
            UUID pointEauId,
            LocalDate dateCalcul,
            double score,
            String bande,
            String confiance,
            Double chargeCumuleeJours,
            Integer intervalleEffectifJours,
            Double indicateurM,
            Double indicateurP,
            Double indicateurS,
            Double indicateurT,
            String facteurs,
            String versionParametrage) {}

    public record AlertePubliee(
            UUID pointEauId,
            String typeRegle,
            String niveau,
            int horizonJours,
            String explication,
            String recommandation,
            String facteurs,
            String versionParametrage) {}

    public record AlerteEmise(UUID alerteId, UUID pointEauId) {}
}
