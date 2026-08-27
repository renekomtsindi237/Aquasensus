package org.aquasensus.prediction.application;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.aquasensus.audit.application.JournalAuditService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ParametrageMoteurService {

    private final JdbcTemplate jdbc;
    private final JournalAuditService audit;

    public ParametrageMoteurService(JdbcTemplate jdbc, JournalAuditService audit) {
        this.jdbc = jdbc;
        this.audit = audit;
    }

    @Transactional(readOnly = true)
    public Version actif() {
        return jdbc.query(
                        "SELECT version, contenu, actif, cree_le FROM parametrage_moteur WHERE actif = TRUE",
                        (rs, row) -> new Version(
                                rs.getString("version"),
                                rs.getString("contenu"),
                                rs.getBoolean("actif"),
                                rs.getTimestamp("cree_le").toInstant()))
                .stream()
                .findFirst()
                .orElse(new Version("v1", "{}", true, Instant.EPOCH));
    }

    @Transactional(readOnly = true)
    public List<Version> historique() {
        return jdbc.query(
                "SELECT version, contenu, actif, cree_le FROM parametrage_moteur ORDER BY cree_le DESC",
                (rs, row) -> new Version(
                        rs.getString("version"),
                        rs.getString("contenu"),
                        rs.getBoolean("actif"),
                        rs.getTimestamp("cree_le").toInstant()));
    }

    @Transactional
    public Version publier(String version, String contenu, UUID acteurId) {
        String ancien = actif().contenu();
        jdbc.update("UPDATE parametrage_moteur SET actif = FALSE");
        jdbc.update(
                "INSERT INTO parametrage_moteur (version, contenu, actif, cree_le) VALUES (?, ?, TRUE, ?)",
                version,
                contenu,
                Instant.now());
        audit.enregistrer(acteurId, "PARAMETRAGE", "MOTEUR", version, ancien, contenu);
        return new Version(version, contenu, true, Instant.now());
    }

    public record Version(String version, String contenu, boolean actif, Instant creeLe) {}
}
