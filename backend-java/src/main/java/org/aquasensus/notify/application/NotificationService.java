package org.aquasensus.notify.application;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.aquasensus.identity.domain.CodeRole;
import org.aquasensus.identity.domain.Utilisateur;
import org.aquasensus.identity.domain.UtilisateurRepository;
import org.aquasensus.messaging.domain.CanalMessage;
import org.aquasensus.messaging.domain.MessageSortant;
import org.aquasensus.messaging.domain.MessagingGateway;
import org.aquasensus.shared.error.RegleMetierException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {

    private final JdbcTemplate jdbc;
    private final UtilisateurRepository utilisateurs;
    private final MessagingGateway gateway;

    public NotificationService(
            JdbcTemplate jdbc, UtilisateurRepository utilisateurs, MessagingGateway gateway) {
        this.jdbc = jdbc;
        this.utilisateurs = utilisateurs;
        this.gateway = gateway;
    }

    @Transactional
    public void notifier(UUID destinataireId, String canal, String evenement, String titre, String corps, String cle) {
        try {
            jdbc.update(
                    """
                    INSERT INTO notification
                    (id, destinataire_id, canal, evenement, titre, corps, statut, cle_dedup, lue, cree_le)
                    VALUES (?, ?, ?, ?, ?, ?, 'EN_ATTENTE', ?, FALSE, ?)
                    """,
                    UUID.randomUUID(),
                    destinataireId,
                    canal,
                    evenement,
                    titre,
                    corps,
                    cle,
                    Instant.now());
        } catch (DataIntegrityViolationException ex) {
            return;
        }
        String statut = "ENVOYEE";
        if ("SMS".equals(canal)) {
            try {
                gateway.envoyer(new MessageSortant(CanalMessage.SMS, "237600000000", corps, null));
            } catch (RegleMetierException ex) {
                statut = "ECHOUEE";
            }
        }
        jdbc.update("UPDATE notification SET statut = ? WHERE cle_dedup = ?", statut, cle);
    }

    @Transactional
    public void auxDeleguesDuComite(UUID comiteId, String evenement, String titre, String corps, String suffixeCle) {
        for (Utilisateur u : utilisateurs.lister()) {
            if (u.comitesPerimetre().contains(comiteId)
                    && (u.possede(CodeRole.DELEGUE) || u.possede(CodeRole.ADMIN))) {
                notifier(u.id(), "IN_APP", evenement, titre, corps, evenement + ":" + u.id() + ":" + suffixeCle);
                notifier(u.id(), "SMS", evenement, titre, corps, evenement + ":SMS:" + u.id() + ":" + suffixeCle);
            }
        }
    }

    @Transactional(readOnly = true)
    public List<NotificationVue> pourUtilisateur(UUID destinataireId) {
        return jdbc.query(
                """
                SELECT id, canal, evenement, titre, corps, statut, lue, cree_le
                FROM notification WHERE destinataire_id = ? ORDER BY cree_le DESC LIMIT 50
                """,
                (rs, row) -> new NotificationVue(
                        (UUID) rs.getObject("id"),
                        rs.getString("canal"),
                        rs.getString("evenement"),
                        rs.getString("titre"),
                        rs.getString("corps"),
                        rs.getString("statut"),
                        rs.getBoolean("lue"),
                        rs.getTimestamp("cree_le").toInstant()),
                destinataireId);
    }

    public record NotificationVue(
            UUID id,
            String canal,
            String evenement,
            String titre,
            String corps,
            String statut,
            boolean lue,
            Instant creeLe) {}
}
