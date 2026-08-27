package org.aquasensus.maintenance.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
class CycleInterventionApiTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void cycleCompletEtKpiRetablissement() throws Exception {
        String admin = jeton("admin@aquasensus.local", "ChangeMoi!2026");
        String delegue = jeton("delegue.a@aquasensus.local", "DelegueA!2026");
        String tech = jeton("tech.a@aquasensus.local", "TechA!2026");
        UUID techId = uuidUtilisateur(tech);

        mvc.perform(post("/api/v1/water-points")
                        .header("Authorization", "Bearer " + admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(fiche("YDE-300")))
                .andExpect(status().isCreated());

        JsonNode point = objectMapper.readTree(mvc.perform(get("/api/v1/water-points"))
                .andReturn()
                .getResponse()
                .getContentAsString());
        String pointId = null;
        for (JsonNode e : point.get("elements")) {
            if ("YDE-300".equals(e.get("code").asText())) {
                pointId = e.get("id").asText();
            }
        }

        MvcResult ouv = mvc.perform(post("/api/v1/interventions")
                        .header("Authorization", "Bearer " + delegue)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"pointEauId":"%s","type":"CORRECTIVE","origine":"MANUELLE","signalementIds":[]}
                                """.formatted(pointId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reference").isNotEmpty())
                .andReturn();
        JsonNode intervention = objectMapper.readTree(ouv.getResponse().getContentAsString());
        String id = intervention.get("id").asText();
        int v = intervention.get("version").asInt();

        MvcResult aff = mvc.perform(post("/api/v1/interventions/" + id + "/affectation")
                        .header("Authorization", "Bearer " + delegue)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"technicienId":"%s","echeanceSouhaitee":"2026-09-01","version":%d}
                                """.formatted(techId, v)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("AFFECTEE"))
                .andReturn();
        v = objectMapper.readTree(aff.getResponse().getContentAsString()).get("version").asInt();

        v = transiter(id, tech, v, "EN_COURS", null);

        MvcResult rapport = mvc.perform(put("/api/v1/interventions/" + id + "/report")
                        .header("Authorization", "Bearer " + tech)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"diagnostic":"Joint usé","causeRacine":"Usure","actions":"Remplacement du joint"}
                                """))
                .andExpect(status().isOk())
                .andReturn();
        v = objectMapper.readTree(rapport.getResponse().getContentAsString()).get("version").asInt();

        MvcResult piece = mvc.perform(post("/api/v1/interventions/" + id + "/parts")
                        .header("Authorization", "Bearer " + tech)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"reference":"JNT-40","libelle":"Joint pompe","quantite":1,"coutUnitaire":2500}
                                """))
                .andExpect(status().isOk())
                .andReturn();
        v = objectMapper.readTree(piece.getResponse().getContentAsString()).get("version").asInt();

        v = transiter(id, tech, v, "REALISEE", """
                ,"diagnostic":"Joint usé","causeRacine":"Usure","actions":"Remplacement du joint"
                """);

        mvc.perform(get("/api/v1/interventions/" + id + "/briefing")
                        .header("Authorization", "Bearer " + tech))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codeOuvrage").value("YDE-300"));

        mvc.perform(post("/api/v1/interventions/" + id + "/transitions")
                        .header("Authorization", "Bearer " + delegue)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"cible":"CLOTUREE","version":%d}
                                """.formatted(v)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("CLOTUREE"))
                .andExpect(jsonPath("$.tempsRetablissementMinutes").isNumber());

        mvc.perform(get("/api/v1/work-queue").header("Authorization", "Bearer " + delegue))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.signalementsAQualifier").isArray());
    }

    @Test
    void transitionInterditeEtVersionConcurrente() throws Exception {
        String admin = jeton("admin@aquasensus.local", "ChangeMoi!2026");
        String delegue = jeton("delegue.a@aquasensus.local", "DelegueA!2026");
        mvc.perform(post("/api/v1/water-points")
                        .header("Authorization", "Bearer " + admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(fiche("YDE-301")))
                .andExpect(status().isCreated());
        String pointId = idPoint("YDE-301");
        MvcResult ouv = mvc.perform(post("/api/v1/interventions")
                        .header("Authorization", "Bearer " + delegue)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"pointEauId":"%s","type":"INSPECTION","origine":"MANUELLE"}
                                """.formatted(pointId)))
                .andExpect(status().isCreated())
                .andReturn();
        JsonNode n = objectMapper.readTree(ouv.getResponse().getContentAsString());
        String id = n.get("id").asText();
        int v = n.get("version").asInt();
        mvc.perform(post("/api/v1/interventions/" + id + "/transitions")
                        .header("Authorization", "Bearer " + delegue)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"cible":"CLOTUREE","version":%d}
                                """.formatted(v)))
                .andExpect(status().isUnprocessableEntity());
        mvc.perform(post("/api/v1/interventions/" + id + "/transitions")
                        .header("Authorization", "Bearer " + delegue)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"cible":"ANNULEE","version":99,"motifAnnulation":"Doublon"}
                                """.formatted()))
                .andExpect(status().isConflict());
    }

    private int transiter(String id, String token, int version, String cible, String extra) throws Exception {
        String extraJson = extra == null ? "" : extra;
        MvcResult r = mvc.perform(post("/api/v1/interventions/" + id + "/transitions")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"cible":"%s","version":%d%s}
                                """.formatted(cible, version, extraJson)))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(r.getResponse().getContentAsString()).get("version").asInt();
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

    private UUID uuidUtilisateur(String jwt) throws Exception {
        String[] parts = jwt.split("\\.");
        String payload = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
        return UUID.fromString(objectMapper.readTree(payload).get("sub").asText());
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
                  "populationDesservie": 200
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
