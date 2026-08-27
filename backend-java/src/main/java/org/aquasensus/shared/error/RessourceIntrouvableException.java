package org.aquasensus.shared.error;

public class RessourceIntrouvableException extends RuntimeException {

    public RessourceIntrouvableException() {
        super("Ressource introuvable.");
    }
}
