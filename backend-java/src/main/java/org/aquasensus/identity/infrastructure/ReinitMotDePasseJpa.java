package org.aquasensus.identity.infrastructure;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReinitMotDePasseJpa extends JpaRepository<ReinitMotDePasseEntity, UUID> {

    List<ReinitMotDePasseEntity> findByIdentifiantOrderByExpireLeDesc(String identifiant);
}
