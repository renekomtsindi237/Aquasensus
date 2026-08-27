package org.aquasensus.prediction.infrastructure;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IndiceSanteJpa extends JpaRepository<IndiceSanteEntity, UUID> {

    Optional<IndiceSanteEntity> findFirstByPointEauIdOrderByDateCalculDesc(UUID pointEauId);

    Optional<IndiceSanteEntity> findByPointEauIdAndDateCalcul(UUID pointEauId, LocalDate dateCalcul);
}
