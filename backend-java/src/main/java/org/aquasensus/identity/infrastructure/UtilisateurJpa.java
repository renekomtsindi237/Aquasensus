package org.aquasensus.identity.infrastructure;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UtilisateurJpa extends JpaRepository<UtilisateurEntity, UUID> {

    java.util.Optional<UtilisateurEntity> findByIdentifiant(String identifiant);
}
