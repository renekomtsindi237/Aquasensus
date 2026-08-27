package org.aquasensus.reporting.infrastructure;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SignalementJpa extends JpaRepository<SignalementEntity, UUID> {

    Optional<SignalementEntity> findByUuidClient(UUID uuidClient);

    Optional<SignalementEntity> findFirstByPointEauIdAndCategorieAndStatutInAndDeclareLeGreaterThanEqualOrderByDeclareLeAsc(
            UUID pointEauId, String categorie, List<String> statuts, Instant depuis);

    List<SignalementEntity> findByDeclarantTelephoneHacheAndDeclareLeGreaterThanEqual(
            String hache, Instant depuis);

    List<SignalementEntity> findByPointEauId(UUID pointEauId);

    @Query(
            """
            select s from SignalementEntity s, org.aquasensus.registry.infrastructure.PointEauEntity p
            where s.pointEauId = p.id and s.statut = 'RECU' and p.comiteId in :comites
            order by s.priorite asc, s.declareLe asc
            """)
    List<SignalementEntity> findAQualifier(@Param("comites") java.util.Collection<UUID> comites);
}
