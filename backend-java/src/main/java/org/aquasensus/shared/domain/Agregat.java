package org.aquasensus.shared.domain;

/**
 * Marqueur d'agrégat. Aucune dépendance technique (DA-02).
 */
public abstract class Agregat {

    private final java.util.UUID id;

    protected Agregat(java.util.UUID id) {
        this.id = java.util.Objects.requireNonNull(id);
    }

    public java.util.UUID id() {
        return id;
    }
}
