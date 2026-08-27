package org.aquasensus.charge.domain;

/** Défauts CDC §9.3 / §16. */
public final class ParametresCharge {

    public static final int INTERVALLE_BASE_JOURS = 180;
    public static final int POPULATION_REFERENCE = 300;
    public static final int INTERVALLE_MIN_JOURS = 90;
    public static final int INTERVALLE_MAX_JOURS = 270;
    public static final double COEFFICIENT_DEFAUT = 1.0;
    public static final String VERSION = "v1";

    private ParametresCharge() {}
}
