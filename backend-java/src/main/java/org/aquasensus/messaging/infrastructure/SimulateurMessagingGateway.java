package org.aquasensus.messaging.infrastructure;

import java.time.Instant;
import java.util.UUID;
import org.aquasensus.messaging.domain.CanalMessage;
import org.aquasensus.messaging.domain.MessageSortant;
import org.aquasensus.messaging.domain.MessagingGateway;
import org.aquasensus.reporting.application.TelephoneDeclarant;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "aquasensus.messaging.adaptateur", havingValue = "simulateur", matchIfMissing = true)
public class SimulateurMessagingGateway implements MessagingGateway {

    private final MessageSimuleJpa journal;

    public SimulateurMessagingGateway(MessageSimuleJpa journal) {
        this.journal = journal;
    }

    @Override
    public void envoyer(MessageSortant message) {
        MessageSimuleEntity e = new MessageSimuleEntity();
        e.setId(UUID.randomUUID());
        e.setDirection("SORTANT");
        e.setCanal(message.canal().name());
        e.setNumeroFictif(message.destinataire());
        e.setNumeroHache(TelephoneDeclarant.hacher(TelephoneDeclarant.normaliser(message.destinataire())));
        e.setContenu(message.contenu());
        e.setSessionId(message.sessionId());
        e.setTraiteLe(Instant.now());
        journal.save(e);
    }

    @Override
    public boolean supporte(CanalMessage canal) {
        return true;
    }
}
