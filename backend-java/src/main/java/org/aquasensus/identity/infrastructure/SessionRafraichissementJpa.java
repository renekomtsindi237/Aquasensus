package org.aquasensus.identity.infrastructure;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRafraichissementJpa extends JpaRepository<SessionRafraichissementEntity, UUID> {

    Optional<SessionRafraichissementEntity> findByJetonHache(String jetonHache);

    java.util.List<SessionRafraichissementEntity> findByUtilisateurId(UUID utilisateurId);
}
