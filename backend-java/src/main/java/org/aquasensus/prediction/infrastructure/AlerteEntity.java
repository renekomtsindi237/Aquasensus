package org.aquasensus.prediction.infrastructure;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "alerte")
public class AlerteEntity {

    @Id
    private UUID id;

    @Column(name = "point_eau_id", nullable = false)
    private UUID pointEauId;

    @Column(name = "type_regle", nullable = false)
    private String typeRegle;

    @Column(nullable = false)
    private String niveau;

    @Column(name = "horizon_jours", nullable = false)
    private int horizonJours;

    @Column(name = "emise_le", nullable = false)
    private Instant emiseLe;

    @Column(nullable = false, length = 2000)
    private String explication;

    @Column(nullable = false, length = 500)
    private String recommandation;

    @Column(nullable = false, length = 4000)
    private String facteurs;

    @Column(nullable = false)
    private String statut;

    @Column(name = "motif_contestation")
    private String motifContestation;

    @Column(name = "reporter_jusqua")
    private LocalDate reporterJusqua;

    private String issue;

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

    public String getTypeRegle() {
        return typeRegle;
    }

    public void setTypeRegle(String typeRegle) {
        this.typeRegle = typeRegle;
    }

    public String getNiveau() {
        return niveau;
    }

    public void setNiveau(String niveau) {
        this.niveau = niveau;
    }

    public int getHorizonJours() {
        return horizonJours;
    }

    public void setHorizonJours(int horizonJours) {
        this.horizonJours = horizonJours;
    }

    public Instant getEmiseLe() {
        return emiseLe;
    }

    public void setEmiseLe(Instant emiseLe) {
        this.emiseLe = emiseLe;
    }

    public String getExplication() {
        return explication;
    }

    public void setExplication(String explication) {
        this.explication = explication;
    }

    public String getRecommandation() {
        return recommandation;
    }

    public void setRecommandation(String recommandation) {
        this.recommandation = recommandation;
    }

    public String getFacteurs() {
        return facteurs;
    }

    public void setFacteurs(String facteurs) {
        this.facteurs = facteurs;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getMotifContestation() {
        return motifContestation;
    }

    public void setMotifContestation(String motifContestation) {
        this.motifContestation = motifContestation;
    }

    public LocalDate getReporterJusqua() {
        return reporterJusqua;
    }

    public void setReporterJusqua(LocalDate reporterJusqua) {
        this.reporterJusqua = reporterJusqua;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public String getVersionParametrage() {
        return versionParametrage;
    }

    public void setVersionParametrage(String versionParametrage) {
        this.versionParametrage = versionParametrage;
    }
}
