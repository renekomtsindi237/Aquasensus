package org.aquasensus.maintenance.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import org.aquasensus.shared.error.RegleMetierException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class MachineEtatsInterventionTest {

    @ParameterizedTest
    @MethodSource("autorisees")
    void transitionsAutorisees(StatutIntervention depart, StatutIntervention arrivee) {
        assertThat(depart.transitions()).contains(arrivee);
    }

    @ParameterizedTest
    @MethodSource("interdites")
    void transitionsInterdites(StatutIntervention depart, StatutIntervention arrivee) {
        assertThatThrownBy(() -> depart.garantirVers(arrivee)).isInstanceOf(RegleMetierException.class);
    }

    static Stream<Arguments> autorisees() {
        return Stream.of(
                Arguments.of(StatutIntervention.OUVERTE, StatutIntervention.AFFECTEE),
                Arguments.of(StatutIntervention.OUVERTE, StatutIntervention.ANNULEE),
                Arguments.of(StatutIntervention.AFFECTEE, StatutIntervention.EN_COURS),
                Arguments.of(StatutIntervention.AFFECTEE, StatutIntervention.ANNULEE),
                Arguments.of(StatutIntervention.EN_COURS, StatutIntervention.SUSPENDUE),
                Arguments.of(StatutIntervention.EN_COURS, StatutIntervention.REALISEE),
                Arguments.of(StatutIntervention.SUSPENDUE, StatutIntervention.EN_COURS),
                Arguments.of(StatutIntervention.SUSPENDUE, StatutIntervention.ANNULEE),
                Arguments.of(StatutIntervention.REALISEE, StatutIntervention.EN_COURS),
                Arguments.of(StatutIntervention.REALISEE, StatutIntervention.CLOTUREE));
    }

    static Stream<Arguments> interdites() {
        return Stream.of(
                Arguments.of(StatutIntervention.OUVERTE, StatutIntervention.EN_COURS),
                Arguments.of(StatutIntervention.OUVERTE, StatutIntervention.CLOTUREE),
                Arguments.of(StatutIntervention.AFFECTEE, StatutIntervention.REALISEE),
                Arguments.of(StatutIntervention.EN_COURS, StatutIntervention.CLOTUREE),
                Arguments.of(StatutIntervention.REALISEE, StatutIntervention.ANNULEE),
                Arguments.of(StatutIntervention.CLOTUREE, StatutIntervention.OUVERTE),
                Arguments.of(StatutIntervention.ANNULEE, StatutIntervention.EN_COURS));
    }

    @Test
    void realiseeRefuseeSansDiagnostic() {
        Intervention i = Intervention.ouvrir(
                "INT-1", UUID.randomUUID(), TypeIntervention.CORRECTIVE, OrigineIntervention.MANUELLE, Set.of());
        i.affecter(UUID.randomUUID(), null);
        i.demarrer();
        assertThatThrownBy(() -> i.declarerRealisee(new CompteRendu(" ", null, "action")))
                .isInstanceOf(RegleMetierException.class)
                .extracting(ex -> ((RegleMetierException) ex).codeRegle())
                .isEqualTo("RG-05");
    }

    @Test
    void clotureRefuseeSiConfirmateurEstTechnicienOuDeclarant() {
        UUID tech = UUID.randomUUID();
        UUID declarant = UUID.randomUUID();
        Intervention i = Intervention.ouvrir(
                "INT-2", UUID.randomUUID(), TypeIntervention.CORRECTIVE, OrigineIntervention.SIGNALEMENT, Set.of());
        i.affecter(tech, null);
        i.demarrer();
        i.declarerRealisee(new CompteRendu("Pompe HS", "Joint", "Remplacement"));
        Instant debut = Instant.parse("2026-08-26T06:00:00Z");
        assertThatThrownBy(() -> i.cloturer(tech, tech, declarant, debut))
                .isInstanceOf(RegleMetierException.class);
        Intervention i2 = Intervention.ouvrir(
                "INT-3", UUID.randomUUID(), TypeIntervention.CORRECTIVE, OrigineIntervention.MANUELLE, Set.of());
        i2.affecter(tech, null);
        i2.demarrer();
        i2.declarerRealisee(new CompteRendu("Pompe HS", "Joint", "Remplacement"));
        assertThatThrownBy(() -> i2.cloturer(declarant, tech, declarant, debut))
                .isInstanceOf(RegleMetierException.class);
    }

    @Test
    void clotureSansSignalementUtiliseOuverture() {
        UUID tech = UUID.randomUUID();
        UUID delegue = UUID.randomUUID();
        Intervention i = Intervention.ouvrir(
                "INT-4", UUID.randomUUID(), TypeIntervention.INSPECTION, OrigineIntervention.MANUELLE, Set.of());
        i.affecter(tech, null);
        i.demarrer();
        i.declarerRealisee(new CompteRendu("RAS", null, "Contrôle visuel"));
        var duree = i.cloturer(delegue, tech, null, null);
        assertThat(i.statut()).isEqualTo(StatutIntervention.CLOTUREE);
        assertThat(i.tempsRetablissementMinutes()).isNotNull();
        assertThat(duree.debut()).isEqualTo(i.ouverteLe());
    }

    @Test
    void suspensionSansMotifRefusee() {
        Intervention i = Intervention.ouvrir(
                "INT-5", UUID.randomUUID(), TypeIntervention.CORRECTIVE, OrigineIntervention.MANUELLE, Set.of());
        i.affecter(UUID.randomUUID(), null);
        i.demarrer();
        assertThatThrownBy(() -> i.suspendre(null)).isInstanceOf(RegleMetierException.class);
    }
}
