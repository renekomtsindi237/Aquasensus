package org.aquasensus.reporting.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CorroborationEtRg02Test {

    @Test
    void unSeulSignalementNeConfirmePasLaPanne() {
        Signalement s = Signalement.ouvrir(
                "SIG-1",
                UUID.randomUUID(),
                UUID.randomUUID(),
                CategorieSymptome.PANNE_TOTALE,
                Gravite.HAUTE,
                null,
                null,
                null,
                null,
                CanalSignalement.WEB,
                Instant.now());
        assertThat(s.panneTotaleConfirmee()).isFalse();
        assertThat(s.nbCorroborations()).isZero();
    }

    @Test
    void deuxCorroborationsConfirmentRg02() {
        UUID ouvrage = UUID.randomUUID();
        Instant t0 = Instant.parse("2026-08-26T08:00:00Z");
        Signalement parent = Signalement.ouvrir(
                "SIG-1",
                UUID.randomUUID(),
                ouvrage,
                CategorieSymptome.PANNE_TOTALE,
                Gravite.HAUTE,
                null,
                null,
                null,
                null,
                CanalSignalement.WEB,
                t0);
        Signalement c1 = Signalement.ouvrir(
                "SIG-2",
                UUID.randomUUID(),
                ouvrage,
                CategorieSymptome.PANNE_TOTALE,
                Gravite.HAUTE,
                null,
                null,
                null,
                null,
                CanalSignalement.MOBILE,
                t0.plusSeconds(60));
        Signalement c2 = Signalement.ouvrir(
                "SIG-3",
                UUID.randomUUID(),
                ouvrage,
                CategorieSymptome.PANNE_TOTALE,
                Gravite.HAUTE,
                null,
                null,
                null,
                null,
                CanalSignalement.WEB,
                t0.plusSeconds(120));
        assertThat(parent.dansFenetreDe(c1)).isTrue();
        c1.corroborerDepuis(parent);
        c2.corroborerDepuis(parent);
        assertThat(parent.nbCorroborations()).isEqualTo(2);
        assertThat(parent.panneTotaleConfirmee()).isTrue();
        assertThat(c1.statut()).isEqualTo(StatutSignalement.DOUBLON);
    }

    @Test
    void categorieDifferenteNEstPasUneCorroboration() {
        UUID ouvrage = UUID.randomUUID();
        Instant t0 = Instant.now();
        Signalement a = Signalement.ouvrir(
                "SIG-a",
                UUID.randomUUID(),
                ouvrage,
                CategorieSymptome.DEBIT_FAIBLE,
                Gravite.MOYENNE,
                null,
                null,
                null,
                null,
                CanalSignalement.WEB,
                t0);
        Signalement b = Signalement.ouvrir(
                "SIG-b",
                UUID.randomUUID(),
                ouvrage,
                CategorieSymptome.PANNE_TOTALE,
                Gravite.HAUTE,
                null,
                null,
                null,
                null,
                CanalSignalement.WEB,
                t0);
        assertThat(a.dansFenetreDe(b)).isFalse();
    }
}
