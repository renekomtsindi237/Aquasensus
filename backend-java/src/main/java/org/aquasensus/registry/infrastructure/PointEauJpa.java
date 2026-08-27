package org.aquasensus.registry.infrastructure;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PointEauJpa extends JpaRepository<PointEauEntity, UUID>, JpaSpecificationExecutor<PointEauEntity> {

    Optional<PointEauEntity> findByCode(String code);

    java.util.List<PointEauEntity> findByActifTrue();
}
