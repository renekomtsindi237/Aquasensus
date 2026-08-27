package org.aquasensus.messaging.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.aquasensus.reporting.domain.CategorieSymptome;
import org.junit.jupiter.api.Test;

class AnalyseurSmsTest {

    @Test
    void decodeTolereCasseEtEspaces() {
        var d = AnalyseurSms.decoder("  aqs   yde-042   panne   plus rien  ").orElseThrow();
        assertThat(d.codePointEau()).isEqualTo("YDE-042");
        assertThat(d.categorie()).isEqualTo(CategorieSymptome.PANNE_TOTALE);
        assertThat(d.commentaire()).contains("plus rien");
    }

    @Test
    void aideSiFormatInvalide() {
        assertThat(AnalyseurSms.decoder("BONJOUR")).isEmpty();
        assertThat(AnalyseurSms.gsm7(AnalyseurSms.AIDE).length()).isLessThanOrEqualTo(160);
        assertThat(AnalyseurSms.gsm7("Risque 🚨")).doesNotContain("🚨");
    }
}
