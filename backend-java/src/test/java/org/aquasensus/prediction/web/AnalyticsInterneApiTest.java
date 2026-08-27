package org.aquasensus.prediction.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
class AnalyticsInterneApiTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void datasetPublieIndiceEtIgnoreAlerteDupliqueePuisConteste() throws Exception {
        String admin = jeton();
        mvc.perform(post("/api/v1/water-points")
                        .header("Authorization", "Bearer " + admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(fiche("YDE-PRED")))
                .andExpect(status().isCreated());
        String pointId = idPoint("YDE-PRED");

        mvc.perform(get("/internal/analytics/dataset")).andExpect(status().isUnauthorized());

        MvcResult ds = mvc.perform(get("/internal/analytics/dataset").header("X-Aqs-Internal-Secret", "test-internal-secret"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ouvrages").isArray())
                .andReturn();
        org.assertj.core.api.Assertions.assertThat(ds.getResponse().getContentAsString().toLowerCase())
                .doesNotContain("litre");

        String score = """
                [{
                  "pointEauId":"%s",
                  "dateCalcul":"%s",
                  "score":72.5,
                  "bande":"SOUS_SURVEILLANCE",
                  "confiance":"MOYENNE",
                  "chargeCumuleeJours":100.0,
                  "intervalleEffectifJours":180,
                  "indicateurM":0.55,
                  "indicateurP":0.2,
                  "indicateurS":0.1,
                  "indicateurT":0.0,
                  "facteurs":"[{\\"code\\":\\"M\\"}]",
                  "versionParametrage":"v1"
                }]
                """.formatted(pointId, LocalDate.now());
        mvc.perform(post("/internal/analytics/health-scores")
                        .header("X-Aqs-Internal-Secret", "test-internal-secret")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(score))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.enregistres").value(1));

        String alerte = """
                [{
                  "pointEauId":"%s",
                  "typeRegle":"R3_FRAGILITE_CHRONIQUE",
                  "niveau":"MODERE",
                  "horizonJours":14,
                  "explication":"Deuxième panne en 3 mois : diagnostic de fond recommandé.",
                  "recommandation":"Planifier un diagnostic de fond.",
                  "facteurs":"[{\\"code\\":\\"P\\",\\"valeur\\":2,\\"seuil\\":2}]",
                  "versionParametrage":"v1"
                }]
                """.formatted(pointId);
        mvc.perform(post("/internal/analytics/alerts")
                        .header("X-Aqs-Internal-Secret", "test-internal-secret")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(alerte))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.enregistres").value(1));
        mvc.perform(post("/internal/analytics/alerts")
                        .header("X-Aqs-Internal-Secret", "test-internal-secret")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(alerte))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.enregistres").value(0));

        JsonNode liste = objectMapper.readTree(mvc.perform(get("/api/v1/alerts")
                        .header("Authorization", "Bearer " + admin))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString());
        String alerteId = liste.get(0).get("id").asText();
        mvc.perform(patch("/api/v1/alerts/" + alerteId)
                        .header("Authorization", "Bearer " + admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"statut":"CONTESTEE","motif":"Contrôle déjà effectué"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("CONTESTEE"))
                .andExpect(jsonPath("$.motifContestation").value("Contrôle déjà effectué"));
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

    private String fiche(String code) {
        return """
                {
                  "code": "%s",
                  "nomUsage": "Forage %s",
                  "type": "FORAGE_MANUEL",
                  "latitude": 3.871000,
                  "longitude": 11.521000,
                  "localiteId": "%s",
                  "comiteId": "%s",
                  "populationDesservie": 400,
                  "dateMiseEnService": "%s"
                }
                """.formatted(
                code,
                code,
                ChargeurDonneesInitiales.LOCALITE_QUARTIER,
                ChargeurDonneesInitiales.COMITE_A,
                LocalDate.now().minusDays(200));
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
