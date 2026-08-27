package org.aquasensus.shared.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aquasensus.shared.config.ChargeurDonneesInitiales;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FinalisationV1ApiTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void importAuditPhotoPdfNotifsEtReferentiels() throws Exception {
        String admin = jeton();
        String csv =
                """
                code,nomUsage,type,latitude,longitude,localiteCode,comiteId,populationDesservie
                YDE-IMP-1,Forage import,FORAGE_MANUEL,3.866700,11.516700,YDE-NKB,%s,450
                incomplet
                YDE-VOL,x,FORAGE_MANUEL,3.866700,11.516700,YDE-NKB,%s,10,volume
                """
                        .formatted(ChargeurDonneesInitiales.COMITE_A, ChargeurDonneesInitiales.COMITE_A);
        MvcResult imp = mvc.perform(post("/api/v1/water-points/import")
                        .header("Authorization", "Bearer " + admin)
                        .contentType(MediaType.parseMediaType("text/csv"))
                        .content(csv))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode lignes = objectMapper.readTree(imp.getResponse().getContentAsString()).get("lignes");
        org.assertj.core.api.Assertions.assertThat(lignes.size()).isGreaterThanOrEqualTo(2);

        mvc.perform(get("/api/v1/audit?entite=POINT_EAU").header("Authorization", "Bearer " + admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].entite").value("POINT_EAU"));

        String pointId = null;
        JsonNode liste = objectMapper.readTree(mvc.perform(get("/api/v1/water-points"))
                .andReturn()
                .getResponse()
                .getContentAsString());
        for (JsonNode e : liste.get("elements")) {
            if ("YDE-IMP-1".equals(e.get("code").asText())) {
                pointId = e.get("id").asText();
            }
        }
        byte[] png = new byte[] {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, 0, 0, 0, 0};
        mvc.perform(multipart("/api/v1/water-points/" + pointId + "/photos")
                        .file(new MockMultipartFile("fichier", "x.exe", "application/octet-stream", png))
                        .header("Authorization", "Bearer " + admin))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.typeMime").value("image/png"));

        mvc.perform(get("/api/v1/dashboard/export.pdf").header("Authorization", "Bearer " + admin))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.startsWith("%PDF")));

        mvc.perform(get("/api/v1/dashboard/budget").header("Authorization", "Bearer " + admin))
                .andExpect(status().isOk());

        mvc.perform(post("/api/v1/reports")
                        .header("X-Client-Request-Id", java.util.UUID.randomUUID().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                {"pointEauCode":"YDE-IMP-1","categorie":"PANNE_TOTALE","gravite":"HAUTE",
                                 "declarantTelephone":"237670000001","codeOtp":"123456"}
                                """))
                .andExpect(status().isCreated());

        mvc.perform(get("/api/v1/notifications").header("Authorization", "Bearer " + admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].evenement").value("SIGNALEMENT_GRAVE"));

        mvc.perform(post("/api/v1/engine/parameters")
                        .header("Authorization", "Bearer " + admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"version\":\"v-test-1\",\"contenu\":\"{\\\"seuilR1\\\":0.9}\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.version").value("v-test-1"));
        mvc.perform(get("/api/v1/engine/parameters/history").header("Authorization", "Bearer " + admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.version=='v1')]").exists());

        mvc.perform(get("/api/v1/admin/symptomes").header("Authorization", "Bearer " + admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("PANNE_TOTALE"));
        mvc.perform(get("/api/v1/admin/types-pieces").header("Authorization", "Bearer " + admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").exists());
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
