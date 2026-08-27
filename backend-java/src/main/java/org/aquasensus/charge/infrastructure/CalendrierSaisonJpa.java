package org.aquasensus.charge.infrastructure;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CalendrierSaisonJpa extends JpaRepository<CalendrierSaisonEntity, UUID> {}
