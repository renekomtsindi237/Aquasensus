package org.aquasensus.maintenance.infrastructure;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterventionSignalementJpa
        extends JpaRepository<InterventionSignalementEntity, InterventionSignalementEntity.Pk> {

    List<InterventionSignalementEntity> findByInterventionId(UUID interventionId);
}
