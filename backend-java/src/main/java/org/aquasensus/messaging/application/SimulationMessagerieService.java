package org.aquasensus.messaging.application;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.aquasensus.messaging.domain.AnalyseurSms;
import org.aquasensus.messaging.domain.CanalMessage;
import org.aquasensus.messaging.domain.MessageSortant;
import org.aquasensus.messaging.domain.MessagingGateway;
import org.aquasensus.messaging.infrastructure.MessageSimuleEntity;
import org.aquasensus.messaging.infrastructure.MessageSimuleJpa;
import org.aquasensus.reporting.application.SignalementService;
import org.aquasensus.reporting.application.TelephoneDeclarant;
import org.aquasensus.reporting.domain.CanalSignalement;
import org.aquasensus.reporting.domain.Gravite;
import org.aquasensus.shared.error.RessourceIntrouvableException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SimulationMessagerieService {

    private final MessageSimuleJpa journal;
    private final MessagingGateway gateway;
    private final SignalementService signalements;

    public SimulationMessagerieService(
            MessageSimuleJpa journal, MessagingGateway gateway, SignalementService signalements) {
        this.journal = journal;
        this.gateway = gateway;
        this.signalements = signalements;
    }

    @Transactional
    public String recevoirSms(String numeroFictif, String contenu) {
        UUID signalementId = null;
        String reponse;
        var decode = AnalyseurSms.decoder(contenu);
        if (decode.isEmpty()) {
            reponse = AnalyseurSms.gsm7(AnalyseurSms.AIDE);
        } else {
            try {
                var r = signalements.declarer(new SignalementService.CommandeSignalement(
                        UUID.randomUUID(),
                        decode.get().codePointEau(),
                        decode.get().categorie(),
                        decode.get().categorie().name().equals("PANNE_TOTALE")
                                ? Gravite.HAUTE
                                : Gravite.MOYENNE,
                        decode.get().commentaire(),
                        CanalSignalement.SMS,
                        numeroFictif,
                        null,
                        Instant.now(),
                        null));
                signalementId = r.enregistre().id();
                reponse = AnalyseurSms.gsm7(
                        "Signalement " + r.incident().reference() + " enregistre. Le comite est averti.");
            } catch (RessourceIntrouvableException ex) {
                reponse = AnalyseurSms.gsm7("Point d'eau inconnu. Verifiez le code (ex: YDE-042).");
            } catch (RuntimeException ex) {
                reponse = AnalyseurSms.gsm7(AnalyseurSms.AIDE);
            }
        }
        journaliser("ENTRANT", CanalMessage.SMS, numeroFictif, contenu, null, signalementId);
        gateway.envoyer(new MessageSortant(CanalMessage.SMS, numeroFictif, reponse, null));
        return reponse;
    }

    @Transactional(readOnly = true)
    public List<MessageJournal> lister() {
        return journal.findAllByOrderByTraiteLeDesc().stream()
                .map(e -> new MessageJournal(
                        e.getId(),
                        e.getDirection(),
                        e.getCanal(),
                        e.getNumeroFictif(),
                        e.getContenu(),
                        e.getSessionId(),
                        e.getTraiteLe()))
                .toList();
    }

    public void journaliser(
            String direction,
            CanalMessage canal,
            String numero,
            String contenu,
            UUID sessionId,
            UUID signalementId) {
        MessageSimuleEntity e = new MessageSimuleEntity();
        e.setId(UUID.randomUUID());
        e.setDirection(direction);
        e.setCanal(canal.name());
        e.setNumeroFictif(numero);
        e.setNumeroHache(TelephoneDeclarant.hacher(TelephoneDeclarant.normaliser(numero)));
        e.setContenu(contenu);
        e.setSessionId(sessionId);
        e.setSignalementId(signalementId);
        e.setTraiteLe(Instant.now());
        journal.save(e);
    }

    public record MessageJournal(
            UUID id,
            String direction,
            String canal,
            String numeroFictif,
            String contenu,
            UUID sessionId,
            Instant traiteLe) {}
}
