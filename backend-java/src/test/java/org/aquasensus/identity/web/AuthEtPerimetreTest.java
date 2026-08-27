package org.aquasensus.identity.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.EnumSet;
import java.util.Set;
import org.aquasensus.identity.domain.CodeRole;
import org.aquasensus.identity.domain.Utilisateur;
import org.aquasensus.identity.domain.UtilisateurRepository;
import org.aquasensus.shared.config.ChargeurDonneesInitiales;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthEtPerimetreTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UtilisateurRepository utilisateurs;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    void santePublique() throws Exception {
        mvc.perform(get("/api/v1/health")).andExpect(status().isOk()).andExpect(jsonPath("$.statut").value("ok"));
    }

    @Test
    void loginRefuseMotDePasseInvalideSansEnumererLeCompte() throws Exception {
        mvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"identifiant":"admin@aquasensus.local","motDePasse":"mauvais"}
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.detail").value("Identifiant ou mot de passe incorrect."));
    }

    @Test
    void loginPuisAccesComiteEtRefusPerimetre() throws Exception {
        MvcResult login = mvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"identifiant":"delegue.a@aquasensus.local","motDePasse":"DelegueA!2026"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jetonAcces").isNotEmpty())
                .andReturn();
        String acces = objectMapper.readTree(login.getResponse().getContentAsString()).get("jetonAcces").asText();

        mvc.perform(get("/api/v1/comites/" + ChargeurDonneesInitiales.COMITE_A)
                        .header("Authorization", "Bearer " + acces))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Comité A — Nkolbisson"));

        mvc.perform(get("/api/v1/comites/" + ChargeurDonneesInitiales.COMITE_B)
                        .header("Authorization", "Bearer " + acces))
                .andExpect(status().isForbidden());
    }

    @Test
    void cinqEchecsVerrouillentSansRevelerLeCompte() throws Exception {
        utilisateurs.enregistrer(Utilisateur.nouveau(
                "verrou@aquasensus.local",
                passwordEncoder.encode("Correcte!2026"),
                "Cible verrouillage",
                EnumSet.of(CodeRole.USAGER),
                Set.of(),
                false));
        for (int i = 0; i < 4; i++) {
            mvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"identifiant":"verrou@aquasensus.local","motDePasse":"faux"}
                                    """))
                    .andExpect(status().isUnauthorized());
        }
        mvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"identifiant":"verrou@aquasensus.local","motDePasse":"faux"}
                                """))
                .andExpect(status().isLocked())
                .andExpect(jsonPath("$.detail").value("Compte temporairement verrouillé. Réessayez plus tard."));
    }
}
