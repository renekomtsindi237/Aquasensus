package org.aquasensus.identity.infrastructure;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "reinit_mot_de_passe")
public class ReinitMotDePasseEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String identifiant;

    @Column(name = "code_hache", nullable = false)
    private String codeHache;

    @Column(name = "expire_le", nullable = false)
    private Instant expireLe;

    @Column(nullable = false)
    private boolean consomme;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getIdentifiant() {
        return identifiant;
    }

    public void setIdentifiant(String identifiant) {
        this.identifiant = identifiant;
    }

    public String getCodeHache() {
        return codeHache;
    }

    public void setCodeHache(String codeHache) {
        this.codeHache = codeHache;
    }

    public Instant getExpireLe() {
        return expireLe;
    }

    public void setExpireLe(Instant expireLe) {
        this.expireLe = expireLe;
    }

    public boolean isConsomme() {
        return consomme;
    }

    public void setConsomme(boolean consomme) {
        this.consomme = consomme;
    }
}
