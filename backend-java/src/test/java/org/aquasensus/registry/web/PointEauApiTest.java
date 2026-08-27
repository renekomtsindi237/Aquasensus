package org.aquasensus.registry.web;

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
class PointEauApiTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void adminCreeFichePubliqueEtRefuseGpsEtDoublon() throws Exception {
        String token = jetonAdmin();
        String corps = """
                {
                  "code": "YDE-042",
                  "nomUsage": "Forage Nkolbisson Marché",
                  "type": "FORAGE_MANUEL",
                  "latitude": 3.866700,
                  "longitude": 11.516700,
                  "localiteId": "%s",
                  "comiteId": "%s",
                  "dateMiseEnService": "2019-03-01",
                  "populationDesservie": 450
                }
                """.formatted(ChargeurDonneesInitiales.LOCALITE_QUARTIER, ChargeurDonneesInitiales.COMITE_A);

        MvcResult cree = mvc.perform(post("/api/v1/water-points")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corps))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.etat").value("OPERATIONNEL"))
                .andExpect(jsonPath("$.localiteChemin").value("Centre / Yaoundé / Nkolbisson"))
                .andReturn();
        String id = objectMapper.readTree(cree.getResponse().getContentAsString()).get("id").asText();

        mvc.perform(get("/api/v1/water-points/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("YDE-042"));

        mvc.perform(get("/api/v1/water-points/" + id + "/history")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].etatNouveau").value("OPERATIONNEL"));

        mvc.perform(post("/api/v1/water-points")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corps))
                .andExpect(status().isConflict());

        String horsEmprise = corps.replace("3.866700", "48.850000").replace("11.516700", "2.350000");
        mvc.perform(post("/api/v1/water-points")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(horsEmprise.replace("YDE-042", "YDE-099")))
                .andExpect(status().isUnprocessableEntity());

        mvc.perform(get("/api/v1/localites?niveau=QUARTIER")).andExpect(status().isOk());
    }

    private String jetonAdmin() throws Exception {
        MvcResult login = mvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"identifiant":"admin@aquasensus.local","motDePasse":"ChangeMoi!2026"}
                                """))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(login.getResponse().getContentAsString()).get("jetonAcces").asText();
    }
}
