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
@Table(name = "utilisateur_role")
@IdClass(UtilisateurRoleEntity.Pk.class)
public class UtilisateurRoleEntity {

    @Id
    @Column(name = "utilisateur_id")
    private UUID utilisateurId;

    @Id
    @Column(name = "role_code")
    private String roleCode;

    public UtilisateurRoleEntity() {}

    public UtilisateurRoleEntity(UUID utilisateurId, String roleCode) {
        this.utilisateurId = utilisateurId;
        this.roleCode = roleCode;
    }

    public UUID getUtilisateurId() {
        return utilisateurId;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public static class Pk implements Serializable {
        private UUID utilisateurId;
        private String roleCode;

        public Pk() {}

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Pk pk)) {
                return false;
            }
            return Objects.equals(utilisateurId, pk.utilisateurId) && Objects.equals(roleCode, pk.roleCode);
        }

        @Override
        public int hashCode() {
            return Objects.hash(utilisateurId, roleCode);
        }
    }
}
