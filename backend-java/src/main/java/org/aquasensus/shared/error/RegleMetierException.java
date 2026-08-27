package org.aquasensus.shared.error;

public class RegleMetierException extends RuntimeException {

    private final String codeRegle;

    public RegleMetierException(String codeRegle, String message) {
        super(message);
        this.codeRegle = codeRegle;
    }

    public String codeRegle() {
        return codeRegle;
    }
}
