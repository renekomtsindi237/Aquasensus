package org.aquasensus.shared.error;

public class QuotaDepasseException extends RuntimeException {

    public QuotaDepasseException() {
        super("Trop de signalements depuis ce numéro. Réessayez dans une heure.");
    }
}
