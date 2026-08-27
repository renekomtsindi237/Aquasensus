package org.aquasensus.maintenance.infrastructure;

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
@Table(name = "intervention")
public class InterventionEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String reference;

    @Column(name = "point_eau_id", nullable = false)
    private UUID pointEauId;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String origine;

    @Column(name = "technicien_id")
    private UUID technicienId;

    @Column(nullable = false)
    private String statut;

    @Column(name = "echeance_souhaitee")
    private LocalDate echeanceSouhaitee;

    @Column(name = "motif_suspension")
    private String motifSuspension;

    @Column(name = "motif_annulation")
    private String motifAnnulation;

    private String diagnostic;

    @Column(name = "cause_racine")
    private String causeRacine;

    private String actions;

    @Column(name = "cout_pieces")
    private BigDecimal coutPieces;

    @Column(name = "cout_main_oeuvre")
    private BigDecimal coutMainOeuvre;

    @Column(name = "ouverte_le", nullable = false)
    private Instant ouverteLe;

    @Column(name = "affectee_le")
    private Instant affecteeLe;

    @Column(name = "demarree_le")
    private Instant demarreeLe;

    @Column(name = "realisee_le")
    private Instant realiseeLe;

    @Column(name = "cloturee_le")
    private Instant clotureeLe;

    @Column(name = "temps_retablissement_minutes")
    private Integer tempsRetablissementMinutes;

    @Column(name = "confirmee_par_id")
    private UUID confirmeeParId;

    @Column(name = "intervention_origine_id")
    private UUID interventionOrigineId;

    @Version
    private Integer version = 0;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public UUID getPointEauId() {
        return pointEauId;
    }

    public void setPointEauId(UUID pointEauId) {
        this.pointEauId = pointEauId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOrigine() {
        return origine;
    }

    public void setOrigine(String origine) {
        this.origine = origine;
    }

    public UUID getTechnicienId() {
        return technicienId;
    }

    public void setTechnicienId(UUID technicienId) {
        this.technicienId = technicienId;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public LocalDate getEcheanceSouhaitee() {
        return echeanceSouhaitee;
    }

    public void setEcheanceSouhaitee(LocalDate echeanceSouhaitee) {
        this.echeanceSouhaitee = echeanceSouhaitee;
    }

    public String getMotifSuspension() {
        return motifSuspension;
    }

    public void setMotifSuspension(String motifSuspension) {
        this.motifSuspension = motifSuspension;
    }

    public String getMotifAnnulation() {
        return motifAnnulation;
    }

    public void setMotifAnnulation(String motifAnnulation) {
        this.motifAnnulation = motifAnnulation;
    }

    public String getDiagnostic() {
        return diagnostic;
    }

    public void setDiagnostic(String diagnostic) {
        this.diagnostic = diagnostic;
    }

    public String getCauseRacine() {
        return causeRacine;
    }

    public void setCauseRacine(String causeRacine) {
        this.causeRacine = causeRacine;
    }

    public String getActions() {
        return actions;
    }

    public void setActions(String actions) {
        this.actions = actions;
    }

    public Instant getOuverteLe() {
        return ouverteLe;
    }

    public void setOuverteLe(Instant ouverteLe) {
        this.ouverteLe = ouverteLe;
    }

    public Instant getAffecteeLe() {
        return affecteeLe;
    }

    public void setAffecteeLe(Instant affecteeLe) {
        this.affecteeLe = affecteeLe;
    }

    public Instant getDemarreeLe() {
        return demarreeLe;
    }

    public void setDemarreeLe(Instant demarreeLe) {
        this.demarreeLe = demarreeLe;
    }

    public Instant getRealiseeLe() {
        return realiseeLe;
    }

    public void setRealiseeLe(Instant realiseeLe) {
        this.realiseeLe = realiseeLe;
    }

    public Instant getClotureeLe() {
        return clotureeLe;
    }

    public void setClotureeLe(Instant clotureeLe) {
        this.clotureeLe = clotureeLe;
    }

    public Integer getTempsRetablissementMinutes() {
        return tempsRetablissementMinutes;
    }

    public void setTempsRetablissementMinutes(Integer tempsRetablissementMinutes) {
        this.tempsRetablissementMinutes = tempsRetablissementMinutes;
    }

    public UUID getConfirmeeParId() {
        return confirmeeParId;
    }

    public void setConfirmeeParId(UUID confirmeeParId) {
        this.confirmeeParId = confirmeeParId;
    }

    public UUID getInterventionOrigineId() {
        return interventionOrigineId;
    }

    public void setInterventionOrigineId(UUID interventionOrigineId) {
        this.interventionOrigineId = interventionOrigineId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setCoutPieces(BigDecimal coutPieces) {
        this.coutPieces = coutPieces;
    }

    public void setCoutMainOeuvre(BigDecimal coutMainOeuvre) {
        this.coutMainOeuvre = coutMainOeuvre;
    }
}
