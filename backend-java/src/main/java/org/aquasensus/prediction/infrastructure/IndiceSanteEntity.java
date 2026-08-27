package org.aquasensus.prediction.infrastructure;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "indice_sante")
public class IndiceSanteEntity {

    @Id
    private UUID id;

    @Column(name = "point_eau_id", nullable = false)
    private UUID pointEauId;

    @Column(name = "date_calcul", nullable = false)
    private LocalDate dateCalcul;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal score;

    @Column(nullable = false)
    private String bande;

    @Column(nullable = false)
    private String confiance;

    @Column(name = "charge_cumulee_jours", precision = 10, scale = 2)
    private BigDecimal chargeCumuleeJours;

    @Column(name = "intervalle_effectif_jours")
    private Integer intervalleEffectifJours;

    @Column(name = "indicateur_m", precision = 6, scale = 4)
    private BigDecimal indicateurM;

    @Column(name = "indicateur_p", precision = 8, scale = 4)
    private BigDecimal indicateurP;

    @Column(name = "indicateur_s", precision = 8, scale = 4)
    private BigDecimal indicateurS;

    @Column(name = "indicateur_t", precision = 8, scale = 4)
    private BigDecimal indicateurT;

    @Column(nullable = false, length = 4000)
    private String facteurs;

    @Column(name = "version_parametrage", nullable = false)
    private String versionParametrage;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getPointEauId() {
        return pointEauId;
    }

    public void setPointEauId(UUID pointEauId) {
        this.pointEauId = pointEauId;
    }

    public LocalDate getDateCalcul() {
        return dateCalcul;
    }

    public void setDateCalcul(LocalDate dateCalcul) {
        this.dateCalcul = dateCalcul;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public String getBande() {
        return bande;
    }

    public void setBande(String bande) {
        this.bande = bande;
    }

    public String getConfiance() {
        return confiance;
    }

    public void setConfiance(String confiance) {
        this.confiance = confiance;
    }

    public BigDecimal getChargeCumuleeJours() {
        return chargeCumuleeJours;
    }

    public void setChargeCumuleeJours(BigDecimal chargeCumuleeJours) {
        this.chargeCumuleeJours = chargeCumuleeJours;
    }

    public Integer getIntervalleEffectifJours() {
        return intervalleEffectifJours;
    }

    public void setIntervalleEffectifJours(Integer intervalleEffectifJours) {
        this.intervalleEffectifJours = intervalleEffectifJours;
    }

    public BigDecimal getIndicateurM() {
        return indicateurM;
    }

    public void setIndicateurM(BigDecimal indicateurM) {
        this.indicateurM = indicateurM;
    }

    public BigDecimal getIndicateurP() {
        return indicateurP;
    }

    public void setIndicateurP(BigDecimal indicateurP) {
        this.indicateurP = indicateurP;
    }

    public BigDecimal getIndicateurS() {
        return indicateurS;
    }

    public void setIndicateurS(BigDecimal indicateurS) {
        this.indicateurS = indicateurS;
    }

    public BigDecimal getIndicateurT() {
        return indicateurT;
    }

    public void setIndicateurT(BigDecimal indicateurT) {
        this.indicateurT = indicateurT;
    }

    public String getFacteurs() {
        return facteurs;
    }

    public void setFacteurs(String facteurs) {
        this.facteurs = facteurs;
    }

    public String getVersionParametrage() {
        return versionParametrage;
    }

    public void setVersionParametrage(String versionParametrage) {
        this.versionParametrage = versionParametrage;
    }
}
