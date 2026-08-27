package org.aquasensus.charge.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Charge cumulée en jours pondérés (EF-30, H-2). Aucun litre.
 */
public final class CalculChargeUsage {

    public ResultatCharge calculer(
            LocalDate aujourdHui,
            LocalDate dernierePreventive,
            LocalDate dateMiseEnService,
            Integer populationDesservie,
            Integer intervalleConstructeur,
            UUID localiteId,
            List<PeriodeSaison> periodes) {
        List<PeriodeSaison> actives = periodes.stream().filter(PeriodeSaison::actif).toList();
        boolean calendrierAbsent = actives.isEmpty();

        LocalDate reference;
        String source;
        if (dernierePreventive != null) {
            reference = dernierePreventive;
            source = "MAINTENANCE";
        } else if (dateMiseEnService != null) {
            reference = dateMiseEnService;
            source = "MISE_EN_SERVICE";
        } else {
            reference = null;
            source = "ABSENTE";
        }

        boolean popAbsente = populationDesservie == null;
        boolean incomplet = popAbsente || reference == null;

        Double charge = null;
        long jours = 0;
        long joursSecs = 0;
        if (reference != null && !reference.isAfter(aujourdHui)) {
            charge = 0.0;
            LocalDate jour = reference.plusDays(1);
            while (!jour.isAfter(aujourdHui)) {
                double k = coefficient(jour.getDayOfYear(), localiteId, actives);
                charge += k;
                jours++;
                if (k > 1.0) {
                    joursSecs++;
                }
                jour = jour.plusDays(1);
            }
        }

        int intervalle = intervalleEffectif(populationDesservie, intervalleConstructeur);
        Double m = (charge != null && intervalle > 0) ? Math.min(1.5, charge / intervalle) : null;

        String explication = composerExplication(
                populationDesservie, jours, joursSecs, calendrierAbsent, charge, intervalle, incomplet);
        String invitation = incomplet
                ? "Complétez la fiche (population desservie et date de mise en service). Aucun volume d'eau n'est demandé."
                : null;
        return new ResultatCharge(
                charge,
                intervalle,
                m,
                jours,
                joursSecs,
                reference,
                source,
                calendrierAbsent,
                incomplet,
                explication,
                invitation);
    }

    public int intervalleEffectif(Integer populationDesservie, Integer intervalleConstructeur) {
        if (intervalleConstructeur != null && intervalleConstructeur > 0) {
            return intervalleConstructeur;
        }
        double pop = populationDesservie == null || populationDesservie == 0
                ? ParametresCharge.POPULATION_REFERENCE
                : populationDesservie;
        double brut = ParametresCharge.INTERVALLE_BASE_JOURS
                * (ParametresCharge.POPULATION_REFERENCE / pop);
        return (int) Math.round(Math.max(
                ParametresCharge.INTERVALLE_MIN_JOURS,
                Math.min(ParametresCharge.INTERVALLE_MAX_JOURS, brut)));
    }

    public double coefficient(int jourAnnee, UUID localiteId, List<PeriodeSaison> periodes) {
        if (localiteId != null) {
            for (PeriodeSaison p : periodes) {
                if (localiteId.equals(p.localiteId()) && p.couvre(jourAnnee)) {
                    return p.coefficient();
                }
            }
        }
        for (PeriodeSaison p : periodes) {
            if (p.localiteId() == null && p.couvre(jourAnnee)) {
                return p.coefficient();
            }
        }
        return ParametresCharge.COEFFICIENT_DEFAUT;
    }

    private static String composerExplication(
            Integer population,
            long jours,
            long joursSecs,
            boolean calendrierAbsent,
            Double charge,
            int intervalle,
            boolean incomplet) {
        if (charge == null) {
            return "Estimation impossible : aucune maintenance préventive ni date de mise en service. Fiche incomplète.";
        }
        String pop = population == null ? "population non renseignée (intervalle par défaut)" : population + " habitants desservis";
        String base = "Estimation fondée sur " + pop + " et " + jours
                + " jours depuis la dernière maintenance, dont " + joursSecs
                + " jours de saison sèche (" + String.format(java.util.Locale.ROOT, "%.1f", charge)
                + " jours pondérés pour un intervalle de " + intervalle + " jours).";
        if (calendrierAbsent) {
            base += " Aucun calendrier saisonnier : coefficient 1,0 partout.";
        }
        if (incomplet) {
            base += " Fiche incomplète : la confiance du calcul est dégradée.";
        }
        return base;
    }
}
