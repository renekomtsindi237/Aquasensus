package org.aquasensus.reporting.web;

import static org.hamcrest.Matchers.contains;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
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
class SignalementApiTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void idempotenceCorroborationEtBasculePanne() throws Exception {
        String admin = jeton("admin@aquasensus.local", "ChangeMoi!2026");
        mvc.perform(post("/api/v1/water-points")
                        .header("Authorization", "Bearer " + admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(fiche("YDE-100")))
                .andExpect(status().isCreated());

        UUID uuid = UUID.fromString("3f2a9c1e-77b4-4a2e-9c31-0a5d1f4b8e22");
        String corps = """
                {
                  "pointEauCode": "YDE-100",
                  "categorie": "PANNE_TOTALE",
                  "gravite": "HAUTE",
                  "canal": "WEB",
                  "declarantTelephone": "+237690000012",
                  "codeOtp": "123456"
                }
                """;
        mvc.perform(post("/api/v1/reports")
                        .header("X-Client-Request-Id", uuid.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corps))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.priseEnCharge.message").exists());

        mvc.perform(post("/api/v1/reports")
                        .header("X-Client-Request-Id", uuid.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corps))
                .andExpect(status().isOk());

        mvc.perform(post("/api/v1/reports")
                        .header("X-Client-Request-Id", UUID.randomUUID().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corps.replace("690000012", "690000013")))
                .andExpect(status().isCreated());
        mvc.perform(post("/api/v1/reports")
                        .header("X-Client-Request-Id", UUID.randomUUID().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corps.replace("690000012", "690000014")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pointEau.etat").value("EN_PANNE"));
    }

    @Test
    void rejetNeChangePasLOuvrage() throws Exception {
        String admin = jeton("admin@aquasensus.local", "ChangeMoi!2026");
        mvc.perform(post("/api/v1/water-points")
                        .header("Authorization", "Bearer " + admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(fiche("YDE-200")))
                .andExpect(status().isCreated());

        MvcResult declare = mvc.perform(post("/api/v1/reports")
                        .header("X-Client-Request-Id", UUID.randomUUID().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "pointEauCode": "YDE-200",
                                  "categorie": "DEBIT_FAIBLE",
                                  "gravite": "MOYENNE",
                                  "canal": "WEB",
                                  "declarantTelephone": "+237690000099",
                                  "codeOtp": "123456"
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn();
        JsonNode json = objectMapper.readTree(declare.getResponse().getContentAsString());
        String id = json.get("id").asText();

        String delegue = jeton("delegue.a@aquasensus.local", "DelegueA!2026");
        mvc.perform(patch("/api/v1/reports/" + id + "/qualification")
                        .header("Authorization", "Bearer " + delegue)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"decision":"REJETE","motif":"Contrôle terrain : pompe OK"}
                                """))
                .andExpect(status().isOk());

        mvc.perform(get("/api/v1/water-points"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.elements[?(@.code=='YDE-200')].etat", contains("OPERATIONNEL")));
    }

    private String fiche(String code) {
        return """
                {
                  "code": "%s",
                  "nomUsage": "Forage %s",
                  "type": "FORAGE_MANUEL",
                  "latitude": 3.870000,
                  "longitude": 11.520000,
                  "localiteId": "%s",
                  "comiteId": "%s",
                  "populationDesservie": 300
                }
                """.formatted(code, code, ChargeurDonneesInitiales.LOCALITE_QUARTIER, ChargeurDonneesInitiales.COMITE_A);
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
