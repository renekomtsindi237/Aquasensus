package org.aquasensus.charge.infrastructure;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "calendrier_saison")
public class CalendrierSaisonEntity {

    @Id
    private UUID id;

    @Column(name = "localite_id")
    private UUID localiteId;

    @Column(nullable = false)
    private String libelle;

    @Column(name = "jour_debut", nullable = false)
    private int jourDebut;

    @Column(name = "jour_fin", nullable = false)
    private int jourFin;

    @Column(nullable = false, precision = 3, scale = 2)
    private BigDecimal coefficient;

    @Column(nullable = false)
    private boolean actif = true;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getLocaliteId() {
        return localiteId;
    }

    public void setLocaliteId(UUID localiteId) {
        this.localiteId = localiteId;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public int getJourDebut() {
        return jourDebut;
    }

    public void setJourDebut(int jourDebut) {
        this.jourDebut = jourDebut;
    }

    public int getJourFin() {
        return jourFin;
    }

    public void setJourFin(int jourFin) {
        this.jourFin = jourFin;
    }

    public BigDecimal getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(BigDecimal coefficient) {
        this.coefficient = coefficient;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }
}
