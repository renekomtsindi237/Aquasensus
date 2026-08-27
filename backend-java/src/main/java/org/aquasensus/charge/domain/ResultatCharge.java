package org.aquasensus.charge.domain;

import java.time.LocalDate;

public record ResultatCharge(
        Double chargeCumuleeJours,
        Integer intervalleEffectifJours,
        Double m,
        long joursCalendaires,
        long joursSaisonSeche,
        LocalDate dateReference,
        String sourceReference,
        boolean calendrierAbsent,
        boolean referentielIncomplet,
        String explication,
        String invitationCorrection) {}
