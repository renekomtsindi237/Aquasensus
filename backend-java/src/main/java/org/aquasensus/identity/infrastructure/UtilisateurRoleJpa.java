package org.aquasensus.identity.infrastructure;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UtilisateurRoleJpa extends JpaRepository<UtilisateurRoleEntity, UtilisateurRoleEntity.Pk> {

    java.util.List<UtilisateurRoleEntity> findByUtilisateurId(UUID utilisateurId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from UtilisateurRoleEntity r where r.utilisateurId = :id")
    void deleteByUtilisateurId(@Param("id") UUID utilisateurId);
}
