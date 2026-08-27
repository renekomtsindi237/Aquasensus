package org.aquasensus.charge.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CalculChargeUsageTest {

    private final CalculChargeUsage calcul = new CalculChargeUsage();
    private static final UUID LOCALITE = UUID.fromString("11111111-1111-1111-1111-111111111111");

    @Test
    void saisonSecheAugmenteLaChargeParRapportHorsSaison() {
        PeriodeSaison seche = new PeriodeSaison(
                UUID.randomUUID(), null, "Sèche", 1, 366, 1.30, true);
        LocalDate fin = LocalDate.of(2026, 1, 20);
        LocalDate debut = fin.minusDays(10);
        ResultatCharge avec = calcul.calculer(fin, debut, null, 300, null, LOCALITE, List.of(seche));
        ResultatCharge sans = calcul.calculer(fin, debut, null, 300, null, LOCALITE, List.of());
        assertThat(avec.chargeCumuleeJours()).isEqualTo(13.0, org.assertj.core.data.Offset.offset(1e-9));
        assertThat(sans.chargeCumuleeJours()).isEqualTo(10.0, org.assertj.core.data.Offset.offset(1e-9));
        assertThat(avec.joursSaisonSeche()).isEqualTo(10);
        assertThat(sans.calendrierAbsent()).isTrue();
        assertThat(sans.explication()).contains("coefficient 1,0");
        assertThat(avec.explication()).doesNotContainIgnoringCase("litre");
    }

    @Test
    void dateReferenceAbsenteNeCalculePasM() {
        ResultatCharge r = calcul.calculer(LocalDate.of(2026, 8, 26), null, null, 400, null, LOCALITE, List.of());
        assertThat(r.chargeCumuleeJours()).isNull();
        assertThat(r.m()).isNull();
        assertThat(r.sourceReference()).isEqualTo("ABSENTE");
        assertThat(r.referentielIncomplet()).isTrue();
        assertThat(r.invitationCorrection()).contains("Aucun volume");
    }

    @Test
    void huitCentsHabitantsOntUneEcheancePlusCourteQueCentCinquante() {
        int i800 = calcul.intervalleEffectif(800, null);
        int i150 = calcul.intervalleEffectif(150, null);
        assertThat(i800).isEqualTo(90);
        assertThat(i150).isEqualTo(270);
        assertThat(i800).isLessThan(i150);
    }

    @Test
    void precoConstructeurPrimeSurLeCalcul() {
        assertThat(calcul.intervalleEffectif(800, 120)).isEqualTo(120);
    }

    @Test
    void populationInconnueUtiliseLIntervalleDeBaseBorne() {
        assertThat(calcul.intervalleEffectif(null, null)).isEqualTo(180);
    }
}
