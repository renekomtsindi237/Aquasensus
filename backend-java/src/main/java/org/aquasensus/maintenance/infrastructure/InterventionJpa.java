package org.aquasensus.maintenance.infrastructure;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InterventionJpa extends JpaRepository<InterventionEntity, UUID> {

    List<InterventionEntity> findByPointEauId(UUID pointEauId);

    @Query(
            """
            select i from InterventionEntity i, org.aquasensus.registry.infrastructure.PointEauEntity p
            where i.pointEauId = p.id and p.comiteId in :comites
              and i.statut in ('OUVERTE','AFFECTEE','EN_COURS','SUSPENDUE','REALISEE')
            """)
    List<InterventionEntity> findActivesParComites(@Param("comites") java.util.Collection<UUID> comites);
}
