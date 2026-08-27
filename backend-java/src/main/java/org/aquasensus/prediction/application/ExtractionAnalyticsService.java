package org.aquasensus.prediction.application;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.aquasensus.charge.application.SanteOuvrageService;
import org.aquasensus.charge.domain.CalendrierSaisonRepository;
import org.aquasensus.charge.domain.ParametresCharge;
import org.aquasensus.charge.domain.PeriodeSaison;
import org.aquasensus.registry.domain.EtatPointEau;
import org.aquasensus.registry.domain.HistoriqueEtat;
import org.aquasensus.registry.domain.PointEau;
import org.aquasensus.registry.domain.PointEauRepository;
import org.aquasensus.reporting.domain.CategorieSymptome;
import org.aquasensus.reporting.domain.Signalement;
import org.aquasensus.reporting.domain.SignalementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExtractionAnalyticsService {

    private static final List<CategorieSymptome> SIGNAUX_FAIBLES = List.of(
            CategorieSymptome.DEBIT_FAIBLE,
            CategorieSymptome.BRUIT_ANORMAL,
            CategorieSymptome.EAU_TROUBLE,
            CategorieSymptome.ATTENTE_EXCESSIVE);

    private final PointEauRepository points;
    private final CalendrierSaisonRepository saisons;
    private final SignalementRepository signalements;
    private final SanteOuvrageService sante;

    public ExtractionAnalyticsService(
            PointEauRepository points,
            CalendrierSaisonRepository saisons,
            SignalementRepository signalements,
            SanteOuvrageService sante) {
        this.points = points;
        this.saisons = saisons;
        this.signalements = signalements;
        this.sante = sante;
    }

    @Transactional(readOnly = true)
    public JeuDonnees extraire() {
        LocalDate aujourdHui = LocalDate.now(ZoneOffset.UTC);
        List<OuvrageDataset> ouvrages = new ArrayList<>();
        for (PointEau p : points.actifs()) {
            LocalDate preventive = sante.dernierePreventive(p.id());
            List<String> pannes = p.historique().stream()
                    .filter(h -> h.etatNouveau() == EtatPointEau.EN_PANNE)
                    .map(h -> h.survenuLe().toString())
                    .toList();
            List<SignalementDataset> sigs = signalements.parPointEau(p.id()).stream()
                    .filter(s -> s.signalementParentId() == null)
                    .map(s -> new SignalementDataset(
                            s.declareLe().toString(),
                            s.categorie().name(),
                            SIGNAUX_FAIBLES.contains(s.categorie())))
                    .toList();
            ouvrages.add(new OuvrageDataset(
                    p.id(),
                    p.code(),
                    p.localiteId(),
                    p.comiteId(),
                    p.populationDesservie(),
                    p.intervalleMaintenanceJours(),
                    p.dateMiseEnService(),
                    p.etat().name(),
                    preventive,
                    pannes,
                    sigs,
                    joursHistorique(p, aujourdHui)));
        }
        List<SaisonDataset> cal = saisons.toutes().stream()
                .filter(PeriodeSaison::actif)
                .map(s -> new SaisonDataset(
                        s.id(), s.localiteId(), s.libelle(), s.jourDebut(), s.jourFin(), s.coefficient()))
                .toList();
        return new JeuDonnees(aujourdHui, ParametresCharge.VERSION, cal, ouvrages);
    }

    private static long joursHistorique(PointEau p, LocalDate aujourdHui) {
        LocalDate debut = p.dateMiseEnService();
        Instant premier = p.historique().stream().map(HistoriqueEtat::survenuLe).min(Instant::compareTo).orElse(null);
        if (premier != null) {
            LocalDate d = LocalDate.ofInstant(premier, ZoneOffset.UTC);
            debut = debut == null || d.isBefore(debut) ? d : debut;
        }
        if (debut == null) {
            return 0;
        }
        return Math.max(0, ChronoUnit.DAYS.between(debut, aujourdHui));
    }

    public record JeuDonnees(
            LocalDate dateCalcul, String versionParametrage, List<SaisonDataset> saisons, List<OuvrageDataset> ouvrages) {}

    public record SaisonDataset(
            UUID id, UUID localiteId, String libelle, int jourDebut, int jourFin, double coefficient) {}

    public record OuvrageDataset(
            UUID id,
            String code,
            UUID localiteId,
            UUID comiteId,
            Integer populationDesservie,
            Integer intervalleMaintenanceJours,
            LocalDate dateMiseEnService,
            String etat,
            LocalDate dernierePreventive,
            List<String> pannesIso,
            List<SignalementDataset> signalements,
            long joursHistorique) {}

    public record SignalementDataset(String declareLe, String categorie, boolean signalFaible) {}
}
