package org.aquasensus.registry.domain;

import java.time.Instant;
import java.util.UUID;

public record HistoriqueEtat(
        UUID id,
        EtatPointEau etatPrecedent,
        EtatPointEau etatNouveau,
        String motif,
        UUID auteurId,
        Instant survenuLe) {}
