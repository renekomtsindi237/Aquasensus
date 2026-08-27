package org.aquasensus.prediction.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.aquasensus.shared.error.RegleMetierException;
import org.junit.jupiter.api.Test;

class AlerteCycleDeVieTest {

    @Test
    void contestationConserveLeMotifSansEffacer() {
        Alerte a = Alerte.emettre(
                UUID.randomUUID(),
                TypeRegleAlerte.R2_DEGRADATION_PROGRESSIVE,
                NiveauAlerte.ELEVE,
                14,
                "Les signalements de débit faible augmentent depuis 3 semaines.",
                "Planifier une inspection de la pompe.",
                "[]",
                "v1");
        a.transiter(StatutAlerte.CONTESTEE, "Pompe contrôlée la semaine dernière", null);
        assertThat(a.statut()).isEqualTo(StatutAlerte.CONTESTEE);
        assertThat(a.motifContestation()).isEqualTo("Pompe contrôlée la semaine dernière");
        assertThatThrownBy(() -> a.transiter(StatutAlerte.ACTIVE, null, null))
                .isInstanceOf(RegleMetierException.class)
                .extracting(ex -> ((RegleMetierException) ex).codeRegle())
                .isEqualTo("RG-07");
    }
}
