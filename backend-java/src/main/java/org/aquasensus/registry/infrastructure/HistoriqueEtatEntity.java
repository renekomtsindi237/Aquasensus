package org.aquasensus.registry.infrastructure;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "historique_etat")
public class HistoriqueEtatEntity {

    @Id
    private UUID id;

    @Column(name = "point_eau_id", nullable = false)
    private UUID pointEauId;

    @Column(name = "etat_precedent")
    private String etatPrecedent;

    @Column(name = "etat_nouveau", nullable = false)
    private String etatNouveau;

    @Column(nullable = false)
    private String motif;

    @Column(name = "auteur_id")
    private UUID auteurId;

    @Column(name = "survenu_le", nullable = false)
    private Instant survenuLe;

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

    public String getEtatPrecedent() {
        return etatPrecedent;
    }

    public void setEtatPrecedent(String etatPrecedent) {
        this.etatPrecedent = etatPrecedent;
    }

    public String getEtatNouveau() {
        return etatNouveau;
    }

    public void setEtatNouveau(String etatNouveau) {
        this.etatNouveau = etatNouveau;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public UUID getAuteurId() {
        return auteurId;
    }

    public void setAuteurId(UUID auteurId) {
        this.auteurId = auteurId;
    }

    public Instant getSurvenuLe() {
        return survenuLe;
    }

    public void setSurvenuLe(Instant survenuLe) {
        this.survenuLe = survenuLe;
    }
}
