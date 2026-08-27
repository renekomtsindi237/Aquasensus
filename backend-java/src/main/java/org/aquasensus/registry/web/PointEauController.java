package org.aquasensus.registry.web;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.aquasensus.registry.application.FichePointEau;
import org.aquasensus.registry.application.ImportPointEauService;
import org.aquasensus.registry.application.PhotoFicheService;
import org.aquasensus.registry.application.PointEauService;
import org.aquasensus.registry.domain.EtatPointEau;
import org.aquasensus.registry.domain.FiltrePointEau;
import org.aquasensus.registry.domain.NiveauLocalite;
import org.aquasensus.registry.domain.PointEau;
import org.aquasensus.identity.web.UtilisateurCourant;
import org.aquasensus.shared.web.PageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/water-points")
public class PointEauController {

    private final PointEauService service;
    private final ImportPointEauService imports;
    private final PhotoFicheService photos;

    public PointEauController(
            PointEauService service, ImportPointEauService imports, PhotoFicheService photos) {
        this.service = service;
        this.imports = imports;
        this.photos = photos;
    }

    @GetMapping
    public PageResponse<PointEauPublic> lister(
            @RequestParam(required = false) UUID localiteId,
            @RequestParam(required = false) NiveauLocalite niveauLocalite,
            @RequestParam(required = false) UUID comiteId,
            @RequestParam(required = false) EtatPointEau etat,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int taille) {
        var filtre = new FiltrePointEau(localiteId, niveauLocalite, comiteId, etat, false, page, taille);
        var elements = service.lister(filtre).stream().map(p -> PointEauPublic.depuis(p, service)).toList();
        return new PageResponse<>(elements, page, Math.min(Math.max(taille, 1), 100));
    }

    @GetMapping("/{id:[0-9a-fA-F-]{36}}")
    public PointEauPublic fiche(@PathVariable UUID id) {
        return PointEauPublic.depuis(service.consulter(id, true), service);
    }

    @GetMapping("/{id:[0-9a-fA-F-]{36}}/history")
    @PreAuthorize("isAuthenticated()")
    public java.util.List<HistoriqueEtatReponse> historique(@PathVariable UUID id) {
        return service.historique(id).stream()
                .map(h -> new HistoriqueEtatReponse(
                        h.etatPrecedent(),
                        h.etatNouveau(),
                        h.motif(),
                        h.auteurId(),
                        h.survenuLe()))
                .toList();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public PointEauPublic creer(
            @RequestBody FichePointEau fiche, @AuthenticationPrincipal UtilisateurCourant courant) {
        PointEau cree = service.creer(fiche, courant.id());
        return PointEauPublic.depuis(cree, service);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public PointEauPublic modifier(@PathVariable UUID id, @RequestBody FichePointEau fiche) {
        return PointEauPublic.depuis(service.modifier(id, fiche), service);
    }

    @PostMapping("/{id}/desactivation")
    @PreAuthorize("hasRole('ADMIN')")
    public PointEauPublic desactiver(@PathVariable UUID id) {
        return PointEauPublic.depuis(service.desactiver(id), service);
    }

    @PostMapping(value = "/import", consumes = {"text/csv", "text/plain"})
    @PreAuthorize("hasRole('ADMIN')")
    public ImportPointEauService.Rapport importerCsv(
            @RequestBody String csv, @AuthenticationPrincipal UtilisateurCourant courant) {
        return imports.importer(csv, courant.id());
    }

    @GetMapping("/{id:[0-9a-fA-F-]{36}}/photos")
    @PreAuthorize("isAuthenticated()")
    public java.util.List<PhotoFicheService.PhotoMeta> photos(@PathVariable UUID id) {
        return photos.lister(id);
    }

    @PostMapping(value = "/{id:[0-9a-fA-F-]{36}}/photos", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN','DELEGUE')")
    @ResponseStatus(HttpStatus.CREATED)
    public PhotoFicheService.PhotoMeta photo(
            @PathVariable UUID id,
            @org.springframework.web.bind.annotation.RequestPart("fichier")
                    org.springframework.web.multipart.MultipartFile fichier,
            @AuthenticationPrincipal UtilisateurCourant courant)
            throws java.io.IOException {
        return photos.enregistrer(id, courant.id(), fichier.getBytes(), fichier.getContentType());
    }

    @GetMapping("/{id:[0-9a-fA-F-]{36}}/photos/{photoId}")
    @PreAuthorize("isAuthenticated()")
    public org.springframework.http.ResponseEntity<byte[]> photoContenu(
            @PathVariable UUID id, @PathVariable UUID photoId) {
        byte[] data = photos.contenu(photoId);
        return org.springframework.http.ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_TYPE, "application/octet-stream")
                .body(data);
    }

    public record PointEauPublic(
            UUID id,
            String code,
            String nomUsage,
            String type,
            BigDecimal latitude,
            BigDecimal longitude,
            UUID localiteId,
            String localiteChemin,
            UUID comiteId,
            LocalDate dateMiseEnService,
            Integer populationDesservie,
            String etat,
            boolean actif,
            boolean referentielComplet) {

        static PointEauPublic depuis(PointEau p, PointEauService service) {
            return new PointEauPublic(
                    p.id(),
                    p.code(),
                    p.nomUsage(),
                    p.type().name(),
                    p.position().latitude(),
                    p.position().longitude(),
                    p.localiteId(),
                    service.cheminLocalite(p.localiteId()),
                    p.comiteId(),
                    p.dateMiseEnService(),
                    p.populationDesservie(),
                    p.etat().name(),
                    p.actif(),
                    p.referentielComplet());
        }
    }

    public record HistoriqueEtatReponse(
            org.aquasensus.registry.domain.EtatPointEau etatPrecedent,
            org.aquasensus.registry.domain.EtatPointEau etatNouveau,
            String motif,
            UUID auteurId,
            java.time.Instant survenuLe) {}
}
