package org.aquasensus.registry.domain;

import java.util.UUID;

public record FiltrePointEau(
        UUID localiteId,
        NiveauLocalite niveauLocalite,
        UUID comiteId,
        EtatPointEau etat,
        boolean inclureInactifs,
        int page,
        int taille) {}
