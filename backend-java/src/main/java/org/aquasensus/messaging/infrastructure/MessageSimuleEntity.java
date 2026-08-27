package org.aquasensus.messaging.infrastructure;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "message_simule")
public class MessageSimuleEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String direction;

    @Column(nullable = false)
    private String canal;

    @Column(name = "numero_fictif", nullable = false)
    private String numeroFictif;

    @Column(name = "numero_hache", nullable = false)
    private String numeroHache;

    @Column(nullable = false, length = 320)
    private String contenu;

    @Column(name = "session_id")
    private UUID sessionId;

    @Column(name = "signalement_id")
    private UUID signalementId;

    @Column(name = "traite_le", nullable = false)
    private Instant traiteLe;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getCanal() {
        return canal;
    }

    public void setCanal(String canal) {
        this.canal = canal;
    }

    public String getNumeroFictif() {
        return numeroFictif;
    }

    public void setNumeroFictif(String numeroFictif) {
        this.numeroFictif = numeroFictif;
    }

    public String getNumeroHache() {
        return numeroHache;
    }

    public void setNumeroHache(String numeroHache) {
        this.numeroHache = numeroHache;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public void setSessionId(UUID sessionId) {
        this.sessionId = sessionId;
    }

    public UUID getSignalementId() {
        return signalementId;
    }

    public void setSignalementId(UUID signalementId) {
        this.signalementId = signalementId;
    }

    public Instant getTraiteLe() {
        return traiteLe;
    }

    public void setTraiteLe(Instant traiteLe) {
        this.traiteLe = traiteLe;
    }
}
