package org.aquasensus.shared.error;

public class IdentifiantsInvalidesException extends RuntimeException {

    public IdentifiantsInvalidesException() {
        super("Identifiant ou mot de passe incorrect.");
    }
}
