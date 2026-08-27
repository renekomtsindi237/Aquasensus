package org.aquasensus.identity.application;

import java.util.Set;
import java.util.UUID;
import org.aquasensus.identity.domain.CodeRole;

public record IdentiteJeton(UUID id, String identifiant, Set<CodeRole> roles) {}
