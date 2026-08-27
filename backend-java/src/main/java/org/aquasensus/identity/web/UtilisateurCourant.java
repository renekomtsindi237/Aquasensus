package org.aquasensus.identity.web;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.aquasensus.identity.domain.CodeRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public record UtilisateurCourant(UUID id, String identifiant, Set<CodeRole> roles) {

    public Collection<? extends GrantedAuthority> autorites() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toSet());
    }
}
