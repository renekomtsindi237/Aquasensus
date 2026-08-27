package org.aquasensus.registry.infrastructure;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoriqueEtatJpa extends JpaRepository<HistoriqueEtatEntity, UUID> {

    List<HistoriqueEtatEntity> findByPointEauIdOrderBySurvenuLeAsc(UUID pointEauId);
}
