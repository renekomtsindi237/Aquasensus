package org.aquasensus.registry.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.aquasensus.shared.domain.Agregat;

/**
 * Agrégat Point d'eau. Pas de volume (H-2, EF-01, ISS-011).
 */
public class PointEau extends Agregat {

    private String code;
    private String nomUsage;
    private TypePointEau type;
    private Coordonnees position;
    private UUID localiteId;
    private UUID comiteId;
    private LocalDate dateMiseEnService;
    private BigDecimal profondeurM;
    private BigDecimal debitNominalLMin;
    private Integer populationDesservie;
    private Integer intervalleMaintenanceJours;
    private EtatPointEau etat;
    private boolean actif;
    private final List<HistoriqueEtat> historique;

    public PointEau(
            UUID id,
            String code,
            String nomUsage,
            TypePointEau type,
            Coordonnees position,
            UUID localiteId,
            UUID comiteId,
            LocalDate dateMiseEnService,
            BigDecimal profondeurM,
            BigDecimal debitNominalLMin,
            Integer populationDesservie,
            Integer intervalleMaintenanceJours,
            EtatPointEau etat,
            boolean actif,
            List<HistoriqueEtat> historique) {
        super(id);
        this.code = Objects.requireNonNull(code);
        this.nomUsage = Objects.requireNonNull(nomUsage);
        this.type = Objects.requireNonNull(type);
        this.position = Objects.requireNonNull(position);
        this.localiteId = Objects.requireNonNull(localiteId);
        this.comiteId = Objects.requireNonNull(comiteId);
        this.dateMiseEnService = dateMiseEnService;
        this.profondeurM = profondeurM;
        this.debitNominalLMin = debitNominalLMin;
        this.populationDesservie = populationDesservie;
        this.intervalleMaintenanceJours = intervalleMaintenanceJours;
        this.etat = Objects.requireNonNull(etat);
        this.actif = actif;
        this.historique = new ArrayList<>(historique);
    }

    public static PointEau mettreEnService(
            String code,
            String nomUsage,
            TypePointEau type,
            Coordonnees position,
            UUID localiteId,
            UUID comiteId,
            LocalDate dateMiseEnService,
            BigDecimal profondeurM,
            BigDecimal debitNominalLMin,
            Integer populationDesservie,
            Integer intervalleMaintenanceJours,
            UUID auteurId) {
        Instant maintenant = Instant.now();
        HistoriqueEtat naissance = new HistoriqueEtat(
                UUID.randomUUID(),
                null,
                EtatPointEau.OPERATIONNEL,
                "Mise en service",
                auteurId,
                maintenant);
        return new PointEau(
                UUID.randomUUID(),
                code,
                nomUsage,
                type,
                position,
                localiteId,
                comiteId,
                dateMiseEnService,
                profondeurM,
                debitNominalLMin,
                populationDesservie,
                intervalleMaintenanceJours,
                EtatPointEau.OPERATIONNEL,
                true,
                List.of(naissance));
    }

    public HistoriqueEtat changerEtat(EtatPointEau nouvelEtat, String motif, UUID auteurId) {
        Objects.requireNonNull(nouvelEtat);
        Objects.requireNonNull(motif);
        etat.garantirTransition(nouvelEtat);
        HistoriqueEtat entree = new HistoriqueEtat(
                UUID.randomUUID(), etat, nouvelEtat, motif, auteurId, Instant.now());
        etat = nouvelEtat;
        historique.add(entree);
        return entree;
    }

    public void appliquerFiche(
            String nomUsage,
            TypePointEau type,
            Coordonnees position,
            UUID localiteId,
            UUID comiteId,
            LocalDate dateMiseEnService,
            BigDecimal profondeurM,
            BigDecimal debitNominalLMin,
            Integer populationDesservie,
            Integer intervalleMaintenanceJours) {
        this.nomUsage = Objects.requireNonNull(nomUsage);
        this.type = Objects.requireNonNull(type);
        this.position = Objects.requireNonNull(position);
        this.localiteId = Objects.requireNonNull(localiteId);
        this.comiteId = Objects.requireNonNull(comiteId);
        this.dateMiseEnService = dateMiseEnService;
        this.profondeurM = profondeurM;
        this.debitNominalLMin = debitNominalLMin;
        this.populationDesservie = populationDesservie;
        this.intervalleMaintenanceJours = intervalleMaintenanceJours;
    }

    public void desactiver() {
        this.actif = false;
    }

    public boolean referentielComplet() {
        return dateMiseEnService != null && populationDesservie != null;
    }

    public String code() {
        return code;
    }

    public String nomUsage() {
        return nomUsage;
    }

    public TypePointEau type() {
        return type;
    }

    public Coordonnees position() {
        return position;
    }

    public UUID localiteId() {
        return localiteId;
    }

    public UUID comiteId() {
        return comiteId;
    }

    public LocalDate dateMiseEnService() {
        return dateMiseEnService;
    }

    public BigDecimal profondeurM() {
        return profondeurM;
    }

    public BigDecimal debitNominalLMin() {
        return debitNominalLMin;
    }

    public Integer populationDesservie() {
        return populationDesservie;
    }

    public Integer intervalleMaintenanceJours() {
        return intervalleMaintenanceJours;
    }

    public EtatPointEau etat() {
        return etat;
    }

    public boolean actif() {
        return actif;
    }

    public List<HistoriqueEtat> historique() {
        return Collections.unmodifiableList(historique);
    }
}
