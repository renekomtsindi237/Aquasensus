package org.aquasensus.maintenance.domain;

import java.time.Duration;
import java.time.Instant;

public record DureeRetablissement(Instant debut, Instant fin, int minutes) {

    public static DureeRetablissement entre(Instant debut, Instant fin) {
        long minutes = Math.max(0, Duration.between(debut, fin).toMinutes());
        return new DureeRetablissement(debut, fin, Math.toIntExact(Math.min(minutes, Integer.MAX_VALUE)));
    }
}
