package org.aquasensus.registry.infrastructure;

import java.util.UUID;
import org.aquasensus.registry.domain.ComiteRepository;
import org.springframework.stereotype.Repository;

@Repository
public class ComiteRepositoryJpa implements ComiteRepository {

    private final ComiteJpa jpa;

    public ComiteRepositoryJpa(ComiteJpa jpa) {
        this.jpa = jpa;
    }

    @Override
    public boolean existe(UUID id) {
        return jpa.existsById(id);
    }
}
