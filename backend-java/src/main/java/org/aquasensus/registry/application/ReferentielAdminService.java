package org.aquasensus.registry.application;

import java.util.List;
import java.util.UUID;
import org.aquasensus.audit.application.JournalAuditService;
import org.aquasensus.registry.domain.Localite;
import org.aquasensus.registry.domain.LocaliteRepository;
import org.aquasensus.registry.domain.NiveauLocalite;
import org.aquasensus.shared.error.ConflitException;
import org.aquasensus.shared.error.RegleMetierException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReferentielAdminService {

    private final LocaliteRepository localites;
    private final JdbcTemplate jdbc;
    private final JournalAuditService audit;

    public ReferentielAdminService(
            LocaliteRepository localites, JdbcTemplate jdbc, JournalAuditService audit) {
        this.localites = localites;
        this.jdbc = jdbc;
        this.audit = audit;
    }

    @Transactional
    public Localite creerLocalite(String code, String nom, NiveauLocalite niveau, UUID parentId, UUID acteurId) {
        localites.parCode(code).ifPresent(l -> {
            throw new ConflitException("Code localité déjà utilisé.");
        });
        if (niveau != NiveauLocalite.REGION && parentId == null) {
            throw new RegleMetierException("EF-84", "Un parent est requis hors région.");
        }
        Localite l = new Localite(UUID.randomUUID(), code, nom, niveau, parentId);
        localites.enregistrer(l);
        audit.enregistrer(acteurId, "CREATION", "LOCALITE", l.id().toString(), null, code);
        return l;
    }

    @Transactional
    public UUID creerComite(String nom, UUID localiteId, UUID acteurId) {
        localites.parId(localiteId).orElseThrow(org.aquasensus.shared.error.RessourceIntrouvableException::new);
        UUID id = UUID.randomUUID();
        jdbc.update(
                "INSERT INTO comite (id, nom, localite_id, actif) VALUES (?, ?, ?, TRUE)",
                id,
                nom,
                localiteId);
        audit.enregistrer(acteurId, "CREATION", "COMITE", id.toString(), null, nom);
        return id;
    }

    @Transactional
    public void desactiverComite(UUID id, UUID acteurId) {
        jdbc.update("UPDATE comite SET actif = FALSE WHERE id = ?", id);
        audit.enregistrer(acteurId, "DESACTIVATION", "COMITE", id.toString(), "actif=true", "actif=false");
    }

    @Transactional(readOnly = true)
    public List<TypePiece> typesPieces() {
        return jdbc.query(
                "SELECT code, libelle, actif FROM type_piece ORDER BY code",
                (rs, row) -> new TypePiece(rs.getString("code"), rs.getString("libelle"), rs.getBoolean("actif")));
    }

    @Transactional
    public void upsertTypePiece(String code, String libelle, boolean actif, UUID acteurId) {
        Integer n = jdbc.queryForObject("SELECT COUNT(*) FROM type_piece WHERE code = ?", Integer.class, code);
        if (n != null && n > 0) {
            jdbc.update("UPDATE type_piece SET libelle = ?, actif = ? WHERE code = ?", libelle, actif, code);
        } else {
            jdbc.update("INSERT INTO type_piece (code, libelle, actif) VALUES (?, ?, ?)", code, libelle, actif);
        }
        audit.enregistrer(acteurId, "UPSERT", "TYPE_PIECE", code, null, libelle);
    }

    @Transactional(readOnly = true)
    public List<String> categoriesSymptomes() {
        return List.of(
                "PANNE_TOTALE",
                "DEBIT_FAIBLE",
                "EAU_TROUBLE",
                "EAU_MALODORANTE",
                "BRUIT_ANORMAL",
                "FUITE",
                "DEGRADATION_OUVRAGE",
                "ATTENTE_EXCESSIVE",
                "AUTRE");
    }

    public record TypePiece(String code, String libelle, boolean actif) {}
}
