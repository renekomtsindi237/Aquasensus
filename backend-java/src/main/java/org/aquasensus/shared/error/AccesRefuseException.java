package org.aquasensus.shared.error;

public class AccesRefuseException extends RuntimeException {

    public AccesRefuseException() {
        super("Accès refusé.");
    }
}
