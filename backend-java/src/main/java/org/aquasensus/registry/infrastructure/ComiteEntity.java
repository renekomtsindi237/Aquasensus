package org.aquasensus.registry.infrastructure;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "comite")
public class ComiteEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String nom;

    @Column(name = "localite_id", nullable = false)
    private UUID localiteId;

    @Column(nullable = false)
    private boolean actif;

    public UUID getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public UUID getLocaliteId() {
        return localiteId;
    }

    public boolean isActif() {
        return actif;
    }
}
