package org.aquasensus.registry.infrastructure;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocaliteJpa extends JpaRepository<LocaliteEntity, UUID> {

    Optional<LocaliteEntity> findByCode(String code);
}
