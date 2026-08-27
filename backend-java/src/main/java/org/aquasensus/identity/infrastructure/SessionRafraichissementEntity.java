package org.aquasensus.identity.infrastructure;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "session_rafraichissement")
public class SessionRafraichissementEntity {

    @Id
    private UUID id;

    @Column(name = "utilisateur_id", nullable = false)
    private UUID utilisateurId;

    @Column(name = "jeton_hache", nullable = false, unique = true)
    private String jetonHache;

    @Column(name = "expire_le", nullable = false)
    private Instant expireLe;

    @Column(nullable = false)
    private boolean revoquee;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUtilisateurId() {
        return utilisateurId;
    }

    public void setUtilisateurId(UUID utilisateurId) {
        this.utilisateurId = utilisateurId;
    }

    public String getJetonHache() {
        return jetonHache;
    }

    public void setJetonHache(String jetonHache) {
        this.jetonHache = jetonHache;
    }

    public Instant getExpireLe() {
        return expireLe;
    }

    public void setExpireLe(Instant expireLe) {
        this.expireLe = expireLe;
    }

    public boolean isRevoquee() {
        return revoquee;
    }

    public void setRevoquee(boolean revoquee) {
        this.revoquee = revoquee;
    }
}
