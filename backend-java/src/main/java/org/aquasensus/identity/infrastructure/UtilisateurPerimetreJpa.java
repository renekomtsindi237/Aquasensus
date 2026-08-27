package org.aquasensus.identity.infrastructure;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UtilisateurPerimetreJpa
        extends JpaRepository<UtilisateurPerimetreEntity, UtilisateurPerimetreEntity.Pk> {

    List<UtilisateurPerimetreEntity> findByUtilisateurId(UUID utilisateurId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from UtilisateurPerimetreEntity p where p.utilisateurId = :id")
    void deleteByUtilisateurId(@Param("id") UUID utilisateurId);
}
