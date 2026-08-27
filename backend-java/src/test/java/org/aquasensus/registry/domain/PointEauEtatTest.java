package org.aquasensus.registry.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.UUID;
import org.aquasensus.shared.error.RegleMetierException;
import org.junit.jupiter.api.Test;

class PointEauEtatTest {

    @Test
    void miseEnServiceOperationnelleEtHistorisee() {
        PointEau p = PointEau.mettreEnService(
                "YDE-001",
                "Forage test",
                TypePointEau.FORAGE_MANUEL,
                new Coordonnees(new BigDecimal("3.87"), new BigDecimal("11.52")),
                UUID.randomUUID(),
                UUID.randomUUID(),
                null,
                null,
                null,
                null,
                null,
                UUID.randomUUID());
        assertThat(p.etat()).isEqualTo(EtatPointEau.OPERATIONNEL);
        assertThat(p.historique()).hasSize(1);
        assertThat(p.historique().getFirst().etatNouveau()).isEqualTo(EtatPointEau.OPERATIONNEL);
    }

    @Test
    void panneTotaleDepuisOperationnel() {
        PointEau p = PointEau.mettreEnService(
                "YDE-002",
                "Forage",
                TypePointEau.FORAGE_MANUEL,
                new Coordonnees(new BigDecimal("3.87"), new BigDecimal("11.52")),
                UUID.randomUUID(),
                UUID.randomUUID(),
                null,
                null,
                null,
                200,
                null,
                UUID.randomUUID());
        p.changerEtat(EtatPointEau.EN_PANNE, "RG-02", UUID.randomUUID());
        assertThat(p.etat()).isEqualTo(EtatPointEau.EN_PANNE);
        assertThat(p.historique()).hasSize(2);
        assertThat(p.historique().getLast().etatNouveau()).isEqualTo(p.etat());
    }

    @Test
    void transitionInterdite() {
        PointEau p = PointEau.mettreEnService(
                "YDE-003",
                "Forage",
                TypePointEau.FORAGE_MANUEL,
                new Coordonnees(new BigDecimal("3.87"), new BigDecimal("11.52")),
                UUID.randomUUID(),
                UUID.randomUUID(),
                null,
                null,
                null,
                null,
                null,
                UUID.randomUUID());
        assertThatThrownBy(() -> p.changerEtat(EtatPointEau.HORS_SERVICE, "x", UUID.randomUUID()))
                .isInstanceOf(RegleMetierException.class);
    }

    @Test
    void gpsHorsEmprise() {
        EmpriseGeographique cameroun = new EmpriseGeographique(
                new BigDecimal("1.65"),
                new BigDecimal("13.08"),
                new BigDecimal("8.45"),
                new BigDecimal("16.20"));
        Coordonnees paris = new Coordonnees(new BigDecimal("48.85"), new BigDecimal("2.35"));
        assertThatThrownBy(() -> paris.garantirEmprise(cameroun)).isInstanceOf(RegleMetierException.class);
    }
}
