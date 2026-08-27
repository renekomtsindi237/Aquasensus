package org.aquasensus.registry.web;

import java.util.List;
import java.util.UUID;
import org.aquasensus.identity.web.UtilisateurCourant;
import org.aquasensus.registry.application.ReferentielAdminService;
import org.aquasensus.registry.domain.Localite;
import org.aquasensus.registry.domain.NiveauLocalite;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
public class ReferentielAdminController {

    private final ReferentielAdminService admin;

    public ReferentielAdminController(ReferentielAdminService admin) {
        this.admin = admin;
    }

    @PostMapping("/localites")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public Localite localite(
            @RequestBody LocaliteRequete req, @AuthenticationPrincipal UtilisateurCourant courant) {
        return admin.creerLocalite(req.code(), req.nom(), req.niveau(), req.parentId(), courant.id());
    }

    @PostMapping("/comites")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public ComiteCree comite(
            @RequestBody ComiteRequete req, @AuthenticationPrincipal UtilisateurCourant courant) {
        return new ComiteCree(admin.creerComite(req.nom(), req.localiteId(), courant.id()));
    }

    @PatchMapping("/comites/{id}/desactivation")
    @PreAuthorize("hasRole('ADMIN')")
    public void desactiverComite(
            @PathVariable UUID id, @AuthenticationPrincipal UtilisateurCourant courant) {
        admin.desactiverComite(id, courant.id());
    }

    @GetMapping("/types-pieces")
    @PreAuthorize("hasRole('ADMIN')")
    public List<ReferentielAdminService.TypePiece> pieces() {
        return admin.typesPieces();
    }

    @PostMapping("/types-pieces")
    @PreAuthorize("hasRole('ADMIN')")
    public void upsertPiece(
            @RequestBody ReferentielAdminService.TypePiece req,
            @AuthenticationPrincipal UtilisateurCourant courant) {
        admin.upsertTypePiece(req.code(), req.libelle(), req.actif(), courant.id());
    }

    @GetMapping("/symptomes")
    @PreAuthorize("hasAnyRole('ADMIN','DELEGUE')")
    public List<String> symptomes() {
        return admin.categoriesSymptomes();
    }

    public record LocaliteRequete(String code, String nom, NiveauLocalite niveau, UUID parentId) {}

    public record ComiteRequete(String nom, UUID localiteId) {}

    public record ComiteCree(UUID id) {}
}
