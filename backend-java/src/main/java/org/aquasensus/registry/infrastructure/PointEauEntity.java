package org.aquasensus.registry.infrastructure;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "point_eau")
public class PointEauEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(name = "nom_usage", nullable = false)
    private String nomUsage;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false, precision = 9, scale = 6)
    private BigDecimal latitude;

    @Column(nullable = false, precision = 9, scale = 6)
    private BigDecimal longitude;

    @Column(name = "localite_id", nullable = false)
    private UUID localiteId;

    @Column(name = "comite_id", nullable = false)
    private UUID comiteId;

    @Column(name = "date_mise_en_service")
    private LocalDate dateMiseEnService;

    @Column(name = "profondeur_m", precision = 6, scale = 2)
    private BigDecimal profondeurM;

    @Column(name = "debit_nominal_l_min", precision = 8, scale = 2)
    private BigDecimal debitNominalLMin;

    @Column(name = "population_desservie")
    private Integer populationDesservie;

    @Column(name = "intervalle_maintenance_jours")
    private Integer intervalleMaintenanceJours;

    @Column(nullable = false)
    private String etat;

    @Column(nullable = false)
    private boolean actif;

    @Version
    private Integer version = 0;

    @Column(name = "cree_le", nullable = false)
    private Instant creeLe;

    @Column(name = "modifie_le", nullable = false)
    private Instant modifieLe;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNomUsage() {
        return nomUsage;
    }

    public void setNomUsage(String nomUsage) {
        this.nomUsage = nomUsage;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public UUID getLocaliteId() {
        return localiteId;
    }

    public void setLocaliteId(UUID localiteId) {
        this.localiteId = localiteId;
    }

    public UUID getComiteId() {
        return comiteId;
    }

    public void setComiteId(UUID comiteId) {
        this.comiteId = comiteId;
    }

    public LocalDate getDateMiseEnService() {
        return dateMiseEnService;
    }

    public void setDateMiseEnService(LocalDate dateMiseEnService) {
        this.dateMiseEnService = dateMiseEnService;
    }

    public BigDecimal getProfondeurM() {
        return profondeurM;
    }

    public void setProfondeurM(BigDecimal profondeurM) {
        this.profondeurM = profondeurM;
    }

    public BigDecimal getDebitNominalLMin() {
        return debitNominalLMin;
    }

    public void setDebitNominalLMin(BigDecimal debitNominalLMin) {
        this.debitNominalLMin = debitNominalLMin;
    }

    public Integer getPopulationDesservie() {
        return populationDesservie;
    }

    public void setPopulationDesservie(Integer populationDesservie) {
        this.populationDesservie = populationDesservie;
    }

    public Integer getIntervalleMaintenanceJours() {
        return intervalleMaintenanceJours;
    }

    public void setIntervalleMaintenanceJours(Integer intervalleMaintenanceJours) {
        this.intervalleMaintenanceJours = intervalleMaintenanceJours;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    public Integer getVersion() {
        return version;
    }

    public Instant getCreeLe() {
        return creeLe;
    }

    public void setCreeLe(Instant creeLe) {
        this.creeLe = creeLe;
    }

    public Instant getModifieLe() {
        return modifieLe;
    }

    public void setModifieLe(Instant modifieLe) {
        this.modifieLe = modifieLe;
    }
}
