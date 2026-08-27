package org.aquasensus.messaging.application;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.aquasensus.messaging.domain.AnalyseurSms;
import org.aquasensus.messaging.domain.CanalMessage;
import org.aquasensus.messaging.domain.MessageSortant;
import org.aquasensus.messaging.domain.MessagingGateway;
import org.aquasensus.reporting.application.SignalementService;
import org.aquasensus.reporting.domain.CanalSignalement;
import org.aquasensus.reporting.domain.CategorieSymptome;
import org.aquasensus.reporting.domain.Gravite;
import org.aquasensus.shared.error.RessourceIntrouvableException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SessionUssdService {

    public static final String MENU =
            "AquaSensus\n1. Signaler un probleme\n2. Etat d'un point d'eau\n3. Mes signalements";

    private final Map<UUID, Session> sessions = new ConcurrentHashMap<>();
    private final Duration ttl;
    private final SimulationMessagerieService journal;
    private final MessagingGateway gateway;
    private final SignalementService signalements;
    private final org.aquasensus.registry.domain.PointEauRepository points;

    public SessionUssdService(
            @Value("${aquasensus.ussd.ttl-seconds:90}") long ttlSecondes,
            SimulationMessagerieService journal,
            MessagingGateway gateway,
            SignalementService signalements,
            org.aquasensus.registry.domain.PointEauRepository points) {
        this.ttl = Duration.ofSeconds(ttlSecondes);
        this.journal = journal;
        this.gateway = gateway;
        this.signalements = signalements;
        this.points = points;
    }

    public ReponseUssd traiter(UUID sessionId, String numero, String saisie) {
        String texte = saisie == null ? "" : saisie.trim();
        if (sessionId == null || "*123#".equalsIgnoreCase(texte.replace(" ", ""))) {
            Session s = new Session(UUID.randomUUID(), numero, Etape.MENU, Instant.now().plus(ttl));
            sessions.put(s.id, s);
            return ecran(s, MENU, false, texte);
        }
        Session s = sessions.get(sessionId);
        if (s == null || Instant.now().isAfter(s.expireLe)) {
            sessions.remove(sessionId);
            return new ReponseUssd(null, AnalyseurSms.gsm7("Session expiree. Composez *123#."), true);
        }
        s.expireLe = Instant.now().plus(ttl);
        return switch (s.etape) {
            case MENU -> menu(s, texte);
            case CODE -> code(s, texte);
            case SYMPTOME -> symptome(s, texte);
            case CONFIRM -> confirmer(s, texte);
            case ETAT -> etat(s, texte);
        };
    }

    private ReponseUssd menu(Session s, String texte) {
        if ("1".equals(texte)) {
            s.etape = Etape.CODE;
            return ecran(s, "Entrez le code du point d'eau (ex: YDE-042)", false, texte);
        }
        if ("2".equals(texte)) {
            s.etape = Etape.ETAT;
            return ecran(s, "Entrez le code du point d'eau", false, texte);
        }
        if ("3".equals(texte)) {
            sessions.remove(s.id);
            return ecran(s, "Consultez un delegue du comite pour le suivi.", true, texte);
        }
        return ecran(s, MENU, false, texte);
    }

    private ReponseUssd code(Session s, String texte) {
        s.codePointEau = texte.toUpperCase();
        s.etape = Etape.SYMPTOME;
        return ecran(
                s,
                "1. Panne totale  2. Debit faible  3. Eau trouble\n4. Bruit  5. Fuite  6. Autre",
                false,
                texte);
    }

    private ReponseUssd symptome(Session s, String texte) {
        s.categorie = switch (texte) {
            case "1" -> CategorieSymptome.PANNE_TOTALE;
            case "2" -> CategorieSymptome.DEBIT_FAIBLE;
            case "3" -> CategorieSymptome.EAU_TROUBLE;
            case "4" -> CategorieSymptome.BRUIT_ANORMAL;
            case "5" -> CategorieSymptome.FUITE;
            default -> CategorieSymptome.AUTRE;
        };
        s.etape = Etape.CONFIRM;
        return ecran(s, "Confirmer ? 1. Oui  2. Non", false, texte);
    }

    private ReponseUssd confirmer(Session s, String texte) {
        if (!"1".equals(texte)) {
            sessions.remove(s.id);
            return ecran(s, "Annule.", true, texte);
        }
        try {
            var r = signalements.declarer(new SignalementService.CommandeSignalement(
                    UUID.randomUUID(),
                    s.codePointEau,
                    s.categorie,
                    s.categorie == CategorieSymptome.PANNE_TOTALE ? Gravite.HAUTE : Gravite.MOYENNE,
                    "USSD",
                    CanalSignalement.USSD,
                    s.numero,
                    null,
                    Instant.now(),
                    null));
            sessions.remove(s.id);
            return ecran(
                    s,
                    AnalyseurSms.gsm7(
                            "Signalement " + r.incident().reference() + " enregistre. Le comite est averti."),
                    true,
                    texte);
        } catch (RessourceIntrouvableException ex) {
            sessions.remove(s.id);
            return ecran(s, "Point d'eau inconnu.", true, texte);
        }
    }

    private ReponseUssd etat(Session s, String texte) {
        sessions.remove(s.id);
        return points.parCode(texte.toUpperCase())
                .map(p -> ecran(s, p.code() + " : " + p.etat().libelle(), true, texte))
                .orElseGet(() -> ecran(s, "Point d'eau inconnu.", true, texte));
    }

    private ReponseUssd ecran(Session s, String ecran, boolean termine, String saisie) {
        journal.journaliser("ENTRANT", CanalMessage.USSD, s.numero, saisie, s.id, null);
        String gsm = AnalyseurSms.gsm7(ecran);
        gateway.envoyer(new MessageSortant(CanalMessage.USSD, s.numero, gsm, s.id));
        return new ReponseUssd(termine ? null : s.id, gsm, termine);
    }

    public record ReponseUssd(UUID sessionId, String ecran, boolean termine) {}

    private enum Etape {
        MENU,
        CODE,
        SYMPTOME,
        CONFIRM,
        ETAT
    }

    private static final class Session {
        private final UUID id;
        private final String numero;
        private Etape etape;
        private Instant expireLe;
        private String codePointEau;
        private CategorieSymptome categorie;

        private Session(UUID id, String numero, Etape etape, Instant expireLe) {
            this.id = id;
            this.numero = numero;
            this.etape = etape;
            this.expireLe = expireLe;
        }
    }
}
