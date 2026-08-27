package org.aquasensus.identity.infrastructure;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "utilisateur_perimetre")
@IdClass(UtilisateurPerimetreEntity.Pk.class)
public class UtilisateurPerimetreEntity {

    @Id
    @Column(name = "utilisateur_id")
    private UUID utilisateurId;

    @Id
    @Column(name = "comite_id")
    private UUID comiteId;

    public UtilisateurPerimetreEntity() {}

    public UtilisateurPerimetreEntity(UUID utilisateurId, UUID comiteId) {
        this.utilisateurId = utilisateurId;
        this.comiteId = comiteId;
    }

    public UUID getUtilisateurId() {
        return utilisateurId;
    }

    public UUID getComiteId() {
        return comiteId;
    }

    public static class Pk implements Serializable {
        private UUID utilisateurId;
        private UUID comiteId;

        public Pk() {}

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Pk pk)) {
                return false;
            }
            return Objects.equals(utilisateurId, pk.utilisateurId) && Objects.equals(comiteId, pk.comiteId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(utilisateurId, comiteId);
        }
    }
}
