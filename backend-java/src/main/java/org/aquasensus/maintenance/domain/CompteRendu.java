package org.aquasensus.maintenance.domain;

public record CompteRendu(String diagnostic, String causeRacine, String actions) {

    public boolean estComplet() {
        return nonVide(diagnostic) && nonVide(actions);
    }

    private static boolean nonVide(String s) {
        return s != null && !s.isBlank();
    }
}
