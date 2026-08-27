package org.aquasensus.analytics.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
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
class CarteEtKpiApiTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void cartePubliquePorteFormeEtLibellePuisKpiSansHorsServiceDansDispo() throws Exception {
        String admin = jeton();
        mvc.perform(post("/api/v1/water-points")
                        .header("Authorization", "Bearer " + admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "code": "YDE-CARTE",
                                  "nomUsage": "Forage carte",
                                  "type": "FORAGE_MANUEL",
                                  "latitude": 3.87,
                                  "longitude": 11.52,
                                  "localiteId": "%s",
                                  "comiteId": "%s",
                                  "populationDesservie": 80
                                }
                                """.formatted(
                                ChargeurDonneesInitiales.LOCALITE_QUARTIER, ChargeurDonneesInitiales.COMITE_A)))
                .andExpect(status().isCreated());
        mvc.perform(get("/api/v1/water-points/map"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.code=='YDE-CARTE')].formeMarqueur").exists())
                .andExpect(jsonPath("$[?(@.code=='YDE-CARTE')].libelleEtat").exists());
        mvc.perform(get("/api/v1/dashboard/kpi").header("Authorization", "Bearer " + admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pointsParEtat").exists())
                .andExpect(jsonPath("$.note").value(org.hamcrest.Matchers.containsString("RG-12")))
                .andExpect(jsonPath("$.note").value(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("litre"))));
        mvc.perform(get("/api/v1/dashboard/export").header("Authorization", "Bearer " + admin))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("indicateur")))
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("@"))));
    }

    private String jeton() throws Exception {
        MvcResult login = mvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"identifiant\":\"admin@aquasensus.local\",\"motDePasse\":\"ChangeMoi!2026\"}"))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(login.getResponse().getContentAsString()).get("jetonAcces").asText();
    }
}
