package org.aquasensus.charge.application;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.UUID;
import org.aquasensus.charge.domain.CalculChargeUsage;
import org.aquasensus.charge.domain.CalendrierSaisonRepository;
import org.aquasensus.charge.domain.ResultatCharge;
import org.aquasensus.identity.application.PolitiqueAcces;
import org.aquasensus.identity.domain.CodeRole;
import org.aquasensus.identity.domain.Utilisateur;
import org.aquasensus.identity.domain.UtilisateurRepository;
import org.aquasensus.maintenance.domain.Intervention;
import org.aquasensus.maintenance.domain.InterventionRepository;
import org.aquasensus.maintenance.domain.StatutIntervention;
import org.aquasensus.maintenance.domain.TypeIntervention;
import org.aquasensus.prediction.domain.IndiceSante;
import org.aquasensus.prediction.domain.IndiceSanteRepository;
import org.aquasensus.registry.domain.PointEau;
import org.aquasensus.registry.domain.PointEauRepository;
import org.aquasensus.shared.error.RessourceIntrouvableException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SanteOuvrageService {

    private final PointEauRepository points;
    private final CalendrierSaisonRepository saisons;
    private final InterventionRepository interventions;
    private final IndiceSanteRepository indices;
    private final UtilisateurRepository utilisateurs;
    private final PolitiqueAcces politiqueAcces;
    private final CalculChargeUsage calcul = new CalculChargeUsage();

    public SanteOuvrageService(
            PointEauRepository points,
            CalendrierSaisonRepository saisons,
            InterventionRepository interventions,
            IndiceSanteRepository indices,
            UtilisateurRepository utilisateurs,
            PolitiqueAcces politiqueAcces) {
        this.points = points;
        this.saisons = saisons;
        this.interventions = interventions;
        this.indices = indices;
        this.utilisateurs = utilisateurs;
        this.politiqueAcces = politiqueAcces;
    }

    @Transactional(readOnly = true)
    public DossierSante dossier(UUID pointEauId, UUID acteurId) {
        PointEau ouvrage = points.parId(pointEauId).orElseThrow(RessourceIntrouvableException::new);
        Utilisateur acteur = utilisateurs.parId(acteurId).orElseThrow(RessourceIntrouvableException::new);
        if (!acteur.possede(CodeRole.ADMIN) && !acteur.possede(CodeRole.PARTENAIRE)) {
            politiqueAcces.exigerComite(acteur, ouvrage.comiteId());
        }
        LocalDate preventive = dernierePreventive(ouvrage.id());
        ResultatCharge charge = calcul.calculer(
                LocalDate.now(ZoneOffset.UTC),
                preventive,
                ouvrage.dateMiseEnService(),
                ouvrage.populationDesservie(),
                ouvrage.intervalleMaintenanceJours(),
                ouvrage.localiteId(),
                saisons.toutes());
        IndiceSante dernier = indices.dernier(ouvrage.id()).orElse(null);
        LocalDate echeance = charge.dateReference() == null || charge.intervalleEffectifJours() == null
                ? null
                : charge.dateReference().plusDays(charge.intervalleEffectifJours());
        return new DossierSante(ouvrage.id(), ouvrage.code(), charge, echeance, dernier);
    }

    public LocalDate dernierePreventive(UUID pointEauId) {
        return interventions.parPointEau(pointEauId).stream()
                .filter(i -> i.type() == TypeIntervention.PREVENTIVE)
                .filter(i -> i.statut() == StatutIntervention.CLOTUREE)
                .map(Intervention::clotureeLe)
                .filter(d -> d != null)
                .map(d -> LocalDate.ofInstant(d, ZoneOffset.UTC))
                .max(LocalDate::compareTo)
                .orElse(null);
    }

    public record DossierSante(
            UUID pointEauId,
            String code,
            ResultatCharge charge,
            LocalDate echeanceMaintenance,
            IndiceSante dernierIndice) {}
}
