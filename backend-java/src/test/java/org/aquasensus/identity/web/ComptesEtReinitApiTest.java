package org.aquasensus.identity.web;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
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
class ComptesEtReinitApiTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void inscriptionUsagerPuisConflitIdentifiant() throws Exception {
        mvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "identifiant": "nouvel.usager@aquasensus.local",
                                  "nomAffichage": "Habitant Nkolbisson",
                                  "motDePasse": "PremierAcces!2026"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.jetonAcces").isNotEmpty())
                .andExpect(jsonPath("$.roles", hasItem("USAGER")))
                .andExpect(jsonPath("$.doitChangerMotDePasse").value(false));

        mvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "identifiant": "nouvel.usager@aquasensus.local",
                                  "nomAffichage": "Doublon",
                                  "motDePasse": "PremierAcces!2026"
                                }
                                """))
                .andExpect(status().isConflict());
    }

    @Test
    void adminCreeComptePuisResetSansEnumeration() throws Exception {
        String admin = jeton();
        MvcResult cree = mvc.perform(post("/api/v1/users")
                        .header("Authorization", "Bearer " + admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "identifiant": "nouveau.tech@aquasensus.local",
                                  "nomAffichage": "Tech nouveau",
                                  "motDePasseTemporaire": "Tempo!2026",
                                  "roles": ["TECHNICIEN"],
                                  "comiteIds": []
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.doitChangerMotDePasse").doesNotExist())
                .andReturn();
        String id = objectMapper.readTree(cree.getResponse().getContentAsString()).get("id").asText();
        mvc.perform(get("/api/v1/users").header("Authorization", "Bearer " + admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.identifiant=='nouveau.tech@aquasensus.local')]").exists());

        MvcResult loginTemp = mvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"identifiant":"nouveau.tech@aquasensus.local","motDePasse":"Tempo!2026"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.doitChangerMotDePasse").value(true))
                .andReturn();
        String jetonTemp = objectMapper.readTree(loginTemp.getResponse().getContentAsString()).get("jetonAcces").asText();
        mvc.perform(post("/api/v1/auth/password/change")
                        .header("Authorization", "Bearer " + jetonTemp)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"actuel":"Tempo!2026","nouveau":"Nouveau!2026"}
                                """))
                .andExpect(status().isNoContent());

        mvc.perform(post("/api/v1/auth/password/reset-request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"identifiant\":\"inconnu@aquasensus.local\"}"))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Si un compte")));
        mvc.perform(post("/api/v1/auth/password/reset-request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"identifiant\":\"nouveau.tech@aquasensus.local\"}"))
                .andExpect(status().isAccepted());
        mvc.perform(post("/api/v1/auth/password/reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"identifiant":"nouveau.tech@aquasensus.local","code":"654321","nouveauMotDePasse":"Reset!2026"}
                                """))
                .andExpect(status().isNoContent());
        mvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"identifiant":"nouveau.tech@aquasensus.local","motDePasse":"Reset!2026"}
                                """))
                .andExpect(status().isOk());

        mvc.perform(patch("/api/v1/users/" + id)
                        .header("Authorization", "Bearer " + admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"statut\":\"SUSPENDU\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("SUSPENDU"));
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
