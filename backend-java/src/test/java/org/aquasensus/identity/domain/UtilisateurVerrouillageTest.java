package org.aquasensus.identity.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.EnumSet;
import java.util.Set;
import org.aquasensus.shared.error.CompteVerrouilleException;
import org.junit.jupiter.api.Test;

class UtilisateurVerrouillageTest {

    @Test
    void cinquiemeEchecVerrouilleLeCompte() {
        Utilisateur u = Utilisateur.nouveau(
                "x@y.z", "hash", "X", EnumSet.of(CodeRole.USAGER), Set.of(), false);
        Instant t0 = Instant.parse("2026-08-26T12:00:00Z");
        for (int i = 0; i < Utilisateur.SEUIL_VERROUILLAGE; i++) {
            u.enregistrerEchec(t0);
        }
        assertThat(u.statut()).isEqualTo(StatutCompte.VERROUILLE);
        assertThatThrownBy(() -> u.garantirAcces(t0.plusSeconds(60)))
                .isInstanceOf(CompteVerrouilleException.class);
        u.garantirAcces(t0.plusSeconds(Utilisateur.DUREE_VERROUILLAGE_MINUTES * 60L + 1));
        assertThat(u.statut()).isEqualTo(StatutCompte.ACTIF);
    }
}
