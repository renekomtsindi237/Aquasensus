package org.aquasensus.shared.config;

import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;
import org.aquasensus.identity.domain.CodeRole;
import org.aquasensus.identity.domain.Utilisateur;
import org.aquasensus.identity.domain.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Jeu de développement uniquement (profils {@code dev} et {@code test}). ISS-004.
 */
@Component
@Profile({"dev", "test", "demo"})
public class ChargeurDonneesInitiales implements ApplicationRunner {

    public static final UUID LOCALITE_DEMO = UUID.fromString("11111111-1111-1111-1111-111111111111");
    public static final UUID LOCALITE_COMMUNE = UUID.fromString("22222222-2222-2222-2222-222222222222");
    public static final UUID LOCALITE_QUARTIER = UUID.fromString("33333333-3333-3333-3333-333333333333");
    public static final UUID COMITE_A = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    public static final UUID COMITE_B = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");

    private final JdbcTemplate jdbc;
    private final UtilisateurRepository utilisateurs;
    private final PasswordEncoder passwordEncoder;
    private final String motDePasseAdmin;

    public ChargeurDonneesInitiales(
            JdbcTemplate jdbc,
            UtilisateurRepository utilisateurs,
            PasswordEncoder passwordEncoder,
            @Value("${aquasensus.admin.password:ChangeMoi!2026}") String motDePasseAdmin) {
        this.jdbc = jdbc;
        this.utilisateurs = utilisateurs;
        this.passwordEncoder = passwordEncoder;
        this.motDePasseAdmin = motDePasseAdmin;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!existe("SELECT COUNT(*) FROM localite WHERE id = ?", LOCALITE_DEMO)) {
            jdbc.update(
                    "INSERT INTO localite (id, code, nom, niveau, parent_id) VALUES (?, 'CM-C', 'Centre', 'REGION', NULL)",
                    LOCALITE_DEMO);
        }
        if (!existe("SELECT COUNT(*) FROM localite WHERE id = ?", LOCALITE_COMMUNE)) {
            jdbc.update(
                    "INSERT INTO localite (id, code, nom, niveau, parent_id) VALUES (?, 'YDE', 'Yaoundé', 'COMMUNE', ?)",
                    LOCALITE_COMMUNE,
                    LOCALITE_DEMO);
        }
        if (!existe("SELECT COUNT(*) FROM localite WHERE id = ?", LOCALITE_QUARTIER)) {
            jdbc.update(
                    "INSERT INTO localite (id, code, nom, niveau, parent_id) VALUES (?, 'YDE-NKB', 'Nkolbisson', 'QUARTIER', ?)",
                    LOCALITE_QUARTIER,
                    LOCALITE_COMMUNE);
        }
        if (!existe("SELECT COUNT(*) FROM comite WHERE id = ?", COMITE_A)) {
            jdbc.update(
                    "INSERT INTO comite (id, nom, localite_id, actif) VALUES (?, 'Comité A — Nkolbisson', ?, TRUE)",
                    COMITE_A,
                    LOCALITE_DEMO);
        }
        if (!existe("SELECT COUNT(*) FROM comite WHERE id = ?", COMITE_B)) {
            jdbc.update(
                    "INSERT INTO comite (id, nom, localite_id, actif) VALUES (?, 'Comité B — Soa', ?, TRUE)",
                    COMITE_B,
                    LOCALITE_DEMO);
        }

        if (utilisateurs.parIdentifiant("admin@aquasensus.local").isEmpty()) {
            utilisateurs.enregistrer(Utilisateur.nouveau(
                    "admin@aquasensus.local",
                    passwordEncoder.encode(motDePasseAdmin),
                    "Administratrice démo",
                    EnumSet.of(CodeRole.ADMIN),
                    Set.of(COMITE_A, COMITE_B),
                    true));
        }
        if (utilisateurs.parIdentifiant("delegue.a@aquasensus.local").isEmpty()) {
            utilisateurs.enregistrer(Utilisateur.nouveau(
                    "delegue.a@aquasensus.local",
                    passwordEncoder.encode("DelegueA!2026"),
                    "Déléguée comité A",
                    EnumSet.of(CodeRole.DELEGUE),
                    Set.of(COMITE_A),
                    false));
        }
        if (utilisateurs.parIdentifiant("tech.a@aquasensus.local").isEmpty()) {
            utilisateurs.enregistrer(Utilisateur.nouveau(
                    "tech.a@aquasensus.local",
                    passwordEncoder.encode("TechA!2026"),
                    "Technicien A",
                    EnumSet.of(CodeRole.TECHNICIEN),
                    Set.of(COMITE_A),
                    false));
        }
    }

    private boolean existe(String sql, UUID id) {
        Integer n = jdbc.queryForObject(sql, Integer.class, id);
        return n != null && n > 0;
    }
}
