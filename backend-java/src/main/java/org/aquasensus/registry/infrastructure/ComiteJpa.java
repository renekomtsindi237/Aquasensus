package org.aquasensus.registry.infrastructure;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComiteJpa extends JpaRepository<ComiteEntity, UUID> {}
