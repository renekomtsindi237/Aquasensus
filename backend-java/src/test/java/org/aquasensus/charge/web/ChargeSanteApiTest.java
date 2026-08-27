package org.aquasensus.charge.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import org.aquasensus.shared.config.ChargeurDonneesInitiales;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ChargeSanteApiTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void santeEnJoursPonderesSansLitreEtCalendrierDefaut() throws Exception {
        String admin = jeton("admin@aquasensus.local", "ChangeMoi!2026");
        String delegue = jeton("delegue.a@aquasensus.local", "DelegueA!2026");
        LocalDate miseEnService = LocalDate.now().minusDays(120);
        mvc.perform(post("/api/v1/water-points")
                        .header("Authorization", "Bearer " + admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(fiche("YDE-800", 800, miseEnService)))
                .andExpect(status().isCreated());
        mvc.perform(post("/api/v1/water-points")
                        .header("Authorization", "Bearer " + admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(fiche("YDE-150", 150, miseEnService)))
                .andExpect(status().isCreated());

        String id800 = idPoint("YDE-800");
        String id150 = idPoint("YDE-150");

        MvcResult s800 = mvc.perform(get("/api/v1/water-points/" + id800 + "/health")
                        .header("Authorization", "Bearer " + delegue))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.unite").value("jours pondérés"))
                .andExpect(jsonPath("$.chargeCumuleeJours").isNumber())
                .andExpect(jsonPath("$.intervalleEffectifJours").value(90))
                .andExpect(jsonPath("$.explication").exists())
                .andReturn();
        String corps = s800.getResponse().getContentAsString();
        org.assertj.core.api.Assertions.assertThat(corps.toLowerCase()).doesNotContain("litre");

        mvc.perform(get("/api/v1/water-points/" + id150 + "/health")
                        .header("Authorization", "Bearer " + delegue))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.intervalleEffectifJours").value(270));

        mvc.perform(get("/api/v1/seasons").header("Authorization", "Bearer " + delegue))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].coefficient").value(1.3));
    }

    private String idPoint(String code) throws Exception {
        JsonNode point = objectMapper.readTree(
                mvc.perform(get("/api/v1/water-points")).andReturn().getResponse().getContentAsString());
        for (JsonNode e : point.get("elements")) {
            if (code.equals(e.get("code").asText())) {
                return e.get("id").asText();
            }
        }
        throw new IllegalStateException(code);
    }

    private String fiche(String code, int pop, LocalDate miseEnService) {
        return """
                {
                  "code": "%s",
                  "nomUsage": "Forage %s",
                  "type": "FORAGE_MANUEL",
                  "latitude": 3.870100,
                  "longitude": 11.520100,
                  "localiteId": "%s",
                  "comiteId": "%s",
                  "populationDesservie": %d,
                  "dateMiseEnService": "%s"
                }
                """.formatted(
                code, code, ChargeurDonneesInitiales.LOCALITE_QUARTIER, ChargeurDonneesInitiales.COMITE_A, pop, miseEnService);
    }

    private String jeton(String identifiant, String mdp) throws Exception {
        MvcResult login = mvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"identifiant\":\"%s\",\"motDePasse\":\"%s\"}".formatted(identifiant, mdp)))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(login.getResponse().getContentAsString()).get("jetonAcces").asText();
    }
}
