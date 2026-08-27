package org.aquasensus.prediction.infrastructure;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AlerteJpa extends JpaRepository<AlerteEntity, UUID> {

    Optional<AlerteEntity> findByPointEauIdAndTypeRegleAndStatut(UUID pointEauId, String typeRegle, String statut);

    List<AlerteEntity> findByStatut(String statut);

    @Query(
            """
            select a from AlerteEntity a, org.aquasensus.registry.infrastructure.PointEauEntity p
            where a.pointEauId = p.id and p.comiteId in :comites and a.statut = 'ACTIVE'
            order by a.emiseLe desc
            """)
    List<AlerteEntity> findActivesParComites(@Param("comites") java.util.Collection<UUID> comites);
}
