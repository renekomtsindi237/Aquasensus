package org.aquasensus.registry.application;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.aquasensus.audit.application.JournalAuditService;
import org.aquasensus.registry.domain.Localite;
import org.aquasensus.registry.domain.LocaliteRepository;
import org.aquasensus.registry.domain.TypePointEau;
import org.aquasensus.shared.error.RegleMetierException;
import org.springframework.stereotype.Service;

@Service
public class ImportPointEauService {

    private final PointEauService points;
    private final LocaliteRepository localites;
    private final JournalAuditService audit;

    public ImportPointEauService(PointEauService points, LocaliteRepository localites, JournalAuditService audit) {
        this.points = points;
        this.localites = localites;
        this.audit = audit;
    }

    public Rapport importer(String csv, UUID auteurId) {
        List<Ligne> lignes = new ArrayList<>();
        String[] rows = csv.split("\\R");
        int debut = 0;
        if (rows.length > 0 && rows[0].toLowerCase().contains("code")) {
            debut = 1;
        }
        for (int i = debut; i < rows.length; i++) {
            String raw = rows[i].trim();
            if (raw.isEmpty()) {
                continue;
            }
            if (raw.toLowerCase().contains("volume") || raw.toLowerCase().contains("litre")) {
                lignes.add(new Ligne(i + 1, false, "Colonne volume interdite (H-2)."));
                continue;
            }
            String[] c = raw.split(",", -1);
            if (c.length < 7) {
                lignes.add(new Ligne(i + 1, false, "Colonnes insuffisantes."));
                continue;
            }
            try {
                Localite loc = localites.parCode(c[5].trim()).orElseThrow(
                        () -> new RegleMetierException("EF-05", "Localité inconnue."));
                FichePointEau fiche = new FichePointEau(
                        c[0].trim(),
                        c[1].trim(),
                        TypePointEau.valueOf(c[2].trim()),
                        new BigDecimal(c[3].trim()),
                        new BigDecimal(c[4].trim()),
                        loc.id(),
                        UUID.fromString(c[6].trim()),
                        null,
                        null,
                        null,
                        c.length > 7 && !c[7].isBlank() ? Integer.parseInt(c[7].trim()) : null,
                        null);
                points.creer(fiche, auteurId);
                lignes.add(new Ligne(i + 1, true, "Créé " + fiche.code()));
            } catch (Exception ex) {
                lignes.add(new Ligne(i + 1, false, ex.getMessage() == null ? "Ligne invalide." : ex.getMessage()));
            }
        }
        audit.enregistrer(auteurId, "IMPORT_CSV", "POINT_EAU", "lot", null, lignes.size() + " lignes");
        return new Rapport(lignes);
    }

    public record Ligne(int numero, boolean ok, String message) {}

    public record Rapport(List<Ligne> lignes) {}
}
