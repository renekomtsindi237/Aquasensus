package org.aquasensus.messaging.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
class SimulationCanalApiTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void smsCreeSignalementEtJournalisePuisUssdExpire() throws Exception {
        String admin = jeton();
        mvc.perform(post("/api/v1/water-points")
                        .header("Authorization", "Bearer " + admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(fiche("YDE-SMS")))
                .andExpect(status().isCreated());

        mvc.perform(post("/api/v1/simulation/sms/inbound")
                        .header("Authorization", "Bearer " + admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"numeroFictif":"237600011122","contenu":"AQS YDE-SMS PANNE plus rien"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gsm7").value(true))
                .andExpect(jsonPath("$.reponse").value(org.hamcrest.Matchers.containsString("SIG-")));

        mvc.perform(post("/api/v1/simulation/sms/inbound")
                        .header("Authorization", "Bearer " + admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"numeroFictif":"237600011122","contenu":"bonjour"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reponse").value(org.hamcrest.Matchers.containsString("AQS")));

        mvc.perform(get("/api/v1/simulation/messages").header("Authorization", "Bearer " + admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].direction").exists());

        MvcResult ouv = mvc.perform(post("/api/v1/simulation/ussd/session")
                        .header("Authorization", "Bearer " + admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"numeroFictif":"237600011133","saisie":"*123#"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.termine").value(false))
                .andReturn();
        String sid = objectMapper.readTree(ouv.getResponse().getContentAsString()).get("sessionId").asText();
        Thread.sleep(1200);
        mvc.perform(post("/api/v1/simulation/ussd/session")
                        .header("Authorization", "Bearer " + admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"sessionId":"%s","numeroFictif":"237600011133","saisie":"1"}
                                """.formatted(sid)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.termine").value(true))
                .andExpect(jsonPath("$.ecran").value(org.hamcrest.Matchers.containsString("expire")));
    }

    private String fiche(String code) {
        return """
                {
                  "code": "%s",
                  "nomUsage": "Forage %s",
                  "type": "FORAGE_MANUEL",
                  "latitude": 3.872000,
                  "longitude": 11.522000,
                  "localiteId": "%s",
                  "comiteId": "%s",
                  "populationDesservie": 120
                }
                """.formatted(code, code, ChargeurDonneesInitiales.LOCALITE_QUARTIER, ChargeurDonneesInitiales.COMITE_A);
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
