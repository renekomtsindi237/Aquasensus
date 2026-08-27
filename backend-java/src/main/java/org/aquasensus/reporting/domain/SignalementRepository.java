package org.aquasensus.reporting.domain;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SignalementRepository {

    Optional<Signalement> parId(UUID id);

    Optional<Signalement> parUuidClient(UUID uuidClient);

    Optional<Signalement> incidentOuvert(UUID pointEauId, CategorieSymptome categorie, Instant depuis);

    List<Signalement> recentsParTelephoneHache(String hache, Instant depuis);

    List<Signalement> parIds(java.util.Collection<UUID> ids);

    List<Signalement> aQualifierPourComites(java.util.Collection<UUID> comiteIds);

    List<Signalement> parPointEau(UUID pointEauId);

    Signalement enregistrer(Signalement signalement);

    long prochaineReference();
}
