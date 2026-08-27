package org.aquasensus.maintenance.infrastructure;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "intervention_signalement")
@IdClass(InterventionSignalementEntity.Pk.class)
public class InterventionSignalementEntity {

    @Id
    @Column(name = "intervention_id")
    private UUID interventionId;

    @Id
    @Column(name = "signalement_id")
    private UUID signalementId;

    public InterventionSignalementEntity() {}

    public InterventionSignalementEntity(UUID interventionId, UUID signalementId) {
        this.interventionId = interventionId;
        this.signalementId = signalementId;
    }

    public UUID getInterventionId() {
        return interventionId;
    }

    public UUID getSignalementId() {
        return signalementId;
    }

    public static class Pk implements Serializable {
        private UUID interventionId;
        private UUID signalementId;

        public Pk() {}

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Pk pk)) {
                return false;
            }
            return Objects.equals(interventionId, pk.interventionId)
                    && Objects.equals(signalementId, pk.signalementId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(interventionId, signalementId);
        }
    }
}
