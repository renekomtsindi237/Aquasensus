package org.aquasensus.shared.error;

public class CompteVerrouilleException extends RuntimeException {

    public CompteVerrouilleException() {
        super("Compte temporairement verrouillé. Réessayez plus tard.");
    }
}
