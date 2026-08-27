package org.aquasensus.maintenance.infrastructure;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PieceRemplaceeJpa extends JpaRepository<PieceRemplaceeEntity, UUID> {

    List<PieceRemplaceeEntity> findByInterventionId(UUID interventionId);
}
