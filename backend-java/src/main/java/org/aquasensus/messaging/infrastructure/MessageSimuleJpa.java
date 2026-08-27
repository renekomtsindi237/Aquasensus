package org.aquasensus.messaging.infrastructure;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageSimuleJpa extends JpaRepository<MessageSimuleEntity, UUID> {

    List<MessageSimuleEntity> findAllByOrderByTraiteLeDesc();
}
