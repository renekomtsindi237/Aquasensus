package org.aquasensus.identity.application;

import java.util.Set;
import org.aquasensus.identity.domain.CodeRole;

public record JetonAuthentification(
        String jetonAcces,
        String jetonRafraichissement,
        String nomAffichage,
        Set<CodeRole> roles,
        boolean doitChangerMotDePasse) {}
