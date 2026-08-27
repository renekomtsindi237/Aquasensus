package org.aquasensus.reporting.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class PrioriteCalculeeTest {

    @Test
    void populationEleveeAugmentePuisGelBloque() {
        Signalement s = Signalement.ouvrir(
                "SIG-P",
                UUID.randomUUID(),
                UUID.randomUUID(),
                CategorieSymptome.DEBIT_FAIBLE,
                Gravite.MOYENNE,
                null,
                null,
                null,
                null,
                CanalSignalement.WEB,
                Instant.now(),
                450);
        assertThat(s.priorite()).isEqualTo(2);
        s.ajouterCorroboration(450);
        assertThat(s.priorite()).isEqualTo(1);
        s.figerPriorite(4, "Accès difficile");
        s.ajouterCorroboration(450);
        assertThat(s.priorite()).isEqualTo(4);
        assertThat(s.prioriteFigee()).isTrue();
    }
}
