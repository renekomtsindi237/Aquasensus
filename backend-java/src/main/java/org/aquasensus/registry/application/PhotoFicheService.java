package org.aquasensus.registry.application;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import org.aquasensus.audit.application.JournalAuditService;
import org.aquasensus.shared.error.RegleMetierException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PhotoFicheService {

    public static final int TAILLE_MAX = 3 * 1024 * 1024;

    private final JdbcTemplate jdbc;
    private final Path repertoire;
    private final JournalAuditService audit;

    public PhotoFicheService(
            JdbcTemplate jdbc,
            @Value("${aquasensus.fichiers.photos:./data/photos}") String repertoire,
            JournalAuditService audit) {
        this.jdbc = jdbc;
        this.repertoire = Path.of(repertoire);
        this.audit = audit;
    }

    @Transactional
    public PhotoMeta enregistrer(UUID pointEauId, UUID auteurId, byte[] contenu, String typeDeclare) {
        if (contenu == null || contenu.length == 0 || contenu.length > TAILLE_MAX) {
            throw new RegleMetierException("ENF-26", "Fichier trop volumineux ou vide (max 3 Mo).");
        }
        String mime = detecter(contenu);
        if (mime == null) {
            throw new RegleMetierException("ENF-26", "Type de fichier non autorisé (JPEG, PNG, WebP).");
        }
        UUID id = UUID.randomUUID();
        String nom = id + extension(mime);
        try {
            Files.createDirectories(repertoire);
            Files.write(repertoire.resolve(nom), contenu);
        } catch (IOException ex) {
            throw new RegleMetierException("ENF-26", "Stockage photo impossible.");
        }
        jdbc.update(
                """
                INSERT INTO photo_fiche (id, point_eau_id, nom_stockage, type_mime, taille_octets, auteur_id, cree_le)
                VALUES (?, ?, ?, ?, ?, ?, NOW())
                """,
                id,
                pointEauId,
                nom,
                mime,
                contenu.length,
                auteurId);
        audit.enregistrer(auteurId, "PHOTO", "POINT_EAU", pointEauId.toString(), null, mime);
        return new PhotoMeta(id, pointEauId, mime, contenu.length);
    }

    @Transactional(readOnly = true)
    public List<PhotoMeta> lister(UUID pointEauId) {
        return jdbc.query(
                "SELECT id, point_eau_id, type_mime, taille_octets FROM photo_fiche WHERE point_eau_id = ?",
                (rs, row) -> new PhotoMeta(
                        (UUID) rs.getObject("id"),
                        (UUID) rs.getObject("point_eau_id"),
                        rs.getString("type_mime"),
                        rs.getInt("taille_octets")),
                pointEauId);
    }

    @Transactional(readOnly = true)
    public byte[] contenu(UUID photoId) {
        String nom = jdbc.queryForObject(
                "SELECT nom_stockage FROM photo_fiche WHERE id = ?", String.class, photoId);
        try {
            return Files.readAllBytes(repertoire.resolve(nom));
        } catch (IOException ex) {
            throw new org.aquasensus.shared.error.RessourceIntrouvableException();
        }
    }

    static String detecter(byte[] b) {
        if (b.length >= 3 && (b[0] & 0xFF) == 0xFF && (b[1] & 0xFF) == 0xD8 && (b[2] & 0xFF) == 0xFF) {
            return "image/jpeg";
        }
        if (b.length >= 8
                && (b[0] & 0xFF) == 0x89
                && b[1] == 0x50
                && b[2] == 0x4E
                && b[3] == 0x47) {
            return "image/png";
        }
        if (b.length >= 12
                && b[0] == 'R'
                && b[1] == 'I'
                && b[2] == 'F'
                && b[3] == 'F'
                && b[8] == 'W'
                && b[9] == 'E'
                && b[10] == 'B'
                && b[11] == 'P') {
            return "image/webp";
        }
        return null;
    }

    private static String extension(String mime) {
        return switch (mime) {
            case "image/jpeg" -> ".jpg";
            case "image/webp" -> ".webp";
            default -> ".png";
        };
    }

    public record PhotoMeta(UUID id, UUID pointEauId, String typeMime, int tailleOctets) {}
}
