package org.aquasensus.reporting.infrastructure;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "signalement")
public class SignalementEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String reference;

    @Column(name = "uuid_client", nullable = false, unique = true)
    private UUID uuidClient;

    @Column(name = "point_eau_id", nullable = false)
    private UUID pointEauId;

    @Column(nullable = false)
    private String categorie;

    @Column(nullable = false)
    private String gravite;

    private String commentaire;

    @Column(name = "declarant_utilisateur_id")
    private UUID declarantUtilisateurId;

    @Column(name = "declarant_telephone_hache")
    private String declarantTelephoneHache;

    @Column(name = "declarant_telephone_suffixe")
    private String declarantTelephoneSuffixe;

    @Column(nullable = false)
    private String canal;

    @Column(nullable = false)
    private String statut;

    @Column(name = "signalement_parent_id")
    private UUID signalementParentId;

    @Column(name = "nb_corroborations", nullable = false)
    private int nbCorroborations;

    @Column(nullable = false)
    private int priorite;

    @Column(name = "priorite_figee", nullable = false)
    private boolean prioriteFigee;

    @Column(name = "motif_qualification")
    private String motifQualification;

    @Column(name = "declare_le", nullable = false)
    private Instant declareLe;

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

    public UUID getUuidClient() {
        return uuidClient;
    }

    public void setUuidClient(UUID uuidClient) {
        this.uuidClient = uuidClient;
    }

    public UUID getPointEauId() {
        return pointEauId;
    }

    public void setPointEauId(UUID pointEauId) {
        this.pointEauId = pointEauId;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public String getGravite() {
        return gravite;
    }

    public void setGravite(String gravite) {
        this.gravite = gravite;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public UUID getDeclarantUtilisateurId() {
        return declarantUtilisateurId;
    }

    public void setDeclarantUtilisateurId(UUID declarantUtilisateurId) {
        this.declarantUtilisateurId = declarantUtilisateurId;
    }

    public String getDeclarantTelephoneHache() {
        return declarantTelephoneHache;
    }

    public void setDeclarantTelephoneHache(String declarantTelephoneHache) {
        this.declarantTelephoneHache = declarantTelephoneHache;
    }

    public String getDeclarantTelephoneSuffixe() {
        return declarantTelephoneSuffixe;
    }

    public void setDeclarantTelephoneSuffixe(String declarantTelephoneSuffixe) {
        this.declarantTelephoneSuffixe = declarantTelephoneSuffixe;
    }

    public String getCanal() {
        return canal;
    }

    public void setCanal(String canal) {
        this.canal = canal;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public UUID getSignalementParentId() {
        return signalementParentId;
    }

    public void setSignalementParentId(UUID signalementParentId) {
        this.signalementParentId = signalementParentId;
    }

    public int getNbCorroborations() {
        return nbCorroborations;
    }

    public void setNbCorroborations(int nbCorroborations) {
        this.nbCorroborations = nbCorroborations;
    }

    public int getPriorite() {
        return priorite;
    }

    public void setPriorite(int priorite) {
        this.priorite = priorite;
    }

    public boolean isPrioriteFigee() {
        return prioriteFigee;
    }

    public void setPrioriteFigee(boolean prioriteFigee) {
        this.prioriteFigee = prioriteFigee;
    }

    public String getMotifQualification() {
        return motifQualification;
    }

    public void setMotifQualification(String motifQualification) {
        this.motifQualification = motifQualification;
    }

    public Instant getDeclareLe() {
        return declareLe;
    }

    public void setDeclareLe(Instant declareLe) {
        this.declareLe = declareLe;
    }
}
