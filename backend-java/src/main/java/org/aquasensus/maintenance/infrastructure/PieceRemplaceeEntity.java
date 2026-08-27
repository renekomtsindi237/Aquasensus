package org.aquasensus.maintenance.infrastructure;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "piece_remplacee")
public class PieceRemplaceeEntity {

    @Id
    private UUID id;

    @Column(name = "intervention_id", nullable = false)
    private UUID interventionId;

    @Column(name = "reference_piece", nullable = false)
    private String referencePiece;

    @Column(nullable = false)
    private String libelle;

    @Column(nullable = false)
    private int quantite;

    @Column(name = "cout_unitaire")
    private BigDecimal coutUnitaire;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getInterventionId() {
        return interventionId;
    }

    public void setInterventionId(UUID interventionId) {
        this.interventionId = interventionId;
    }

    public String getReferencePiece() {
        return referencePiece;
    }

    public void setReferencePiece(String referencePiece) {
        this.referencePiece = referencePiece;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public BigDecimal getCoutUnitaire() {
        return coutUnitaire;
    }

    public void setCoutUnitaire(BigDecimal coutUnitaire) {
        this.coutUnitaire = coutUnitaire;
    }
}
