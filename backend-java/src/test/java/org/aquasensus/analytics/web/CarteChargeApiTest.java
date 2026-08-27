package org.aquasensus.analytics.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.Timestamp;
import java.math.BigDecimal;
import java.util.UUID;
import org.aquasensus.registry.domain.EtatPointEau;
import org.aquasensus.shared.config.ChargeurDonneesInitiales;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CarteChargeApiTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    JdbcTemplate jdbc;

    @Test
    void cinqCentsMarqueursSousDeuxSecondesEnH2() throws Exception {
        Timestamp now = Timestamp.from(java.time.Instant.now());
        EtatPointEau[] etats = EtatPointEau.values();
        for (int i = 0; i < 500; i++) {
            jdbc.update(
                    """
                    INSERT INTO point_eau (
                      id, code, nom_usage, type, latitude, longitude, localite_id, comite_id,
                      population_desservie, etat, actif, version, cree_le, modifie_le)
                    VALUES (?,?,?,?,?,?,?,?,?,?,TRUE,0,?,?)
                    """,
                    UUID.nameUUIDFromBytes(("charge-" + i).getBytes()),
                    "YDE-M%04d".formatted(i),
                    "Ouvrage charge " + i,
                    "FORAGE_MANUEL",
                    BigDecimal.valueOf(3.80 + (i % 50) * 0.001),
                    BigDecimal.valueOf(11.50 + (i / 50) * 0.001),
                    ChargeurDonneesInitiales.LOCALITE_QUARTIER,
                    ChargeurDonneesInitiales.COMITE_A,
                    80,
                    etats[i % etats.length].name(),
                    now,
                    now);
        }
        long t0 = System.nanoTime();
        try {
            mvc.perform(get("/api/v1/water-points/map"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(org.hamcrest.Matchers.greaterThanOrEqualTo(500)));
            long ms = (System.nanoTime() - t0) / 1_000_000;
            assertThat(ms).as("budget CI H2 (cible prod 400 ms P95, voir docs/recette/PERF.md)").isLessThan(2000);
        } finally {
            jdbc.update("DELETE FROM point_eau WHERE code LIKE 'YDE-M%'");
        }
    }
}

