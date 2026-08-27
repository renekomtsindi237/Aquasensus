package org.aquasensus.identity.application;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import org.aquasensus.identity.domain.UtilisateurRepository;
import org.aquasensus.identity.infrastructure.ReinitMotDePasseEntity;
import org.aquasensus.identity.infrastructure.ReinitMotDePasseJpa;
import org.aquasensus.messaging.domain.CanalMessage;
import org.aquasensus.messaging.domain.MessageSortant;
import org.aquasensus.messaging.domain.MessagingGateway;
import org.aquasensus.shared.error.IdentifiantsInvalidesException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReinitMotDePasseService {

    public static final String MESSAGE_UNIVOQUE =
            "Si un compte correspond, un code a été envoyé (canal simulé).";

    private final UtilisateurRepository utilisateurs;
    private final ReinitMotDePasseJpa codes;
    private final PasswordEncoder encoder;
    private final MessagingGateway gateway;
    private final Duration ttl;
    private final String codeFixe;

    public ReinitMotDePasseService(
            UtilisateurRepository utilisateurs,
            ReinitMotDePasseJpa codes,
            PasswordEncoder encoder,
            MessagingGateway gateway,
            @Value("${aquasensus.reset.ttl-minutes:15}") long ttlMinutes,
            @Value("${aquasensus.reset.code-fixe:}") String codeFixe) {
        this.utilisateurs = utilisateurs;
        this.codes = codes;
        this.encoder = encoder;
        this.gateway = gateway;
        this.ttl = Duration.ofMinutes(ttlMinutes);
        this.codeFixe = codeFixe == null ? "" : codeFixe;
    }

    @Transactional
    public void demander(String identifiant) {
        var opt = utilisateurs.parIdentifiant(identifiant);
        if (opt.isPresent()) {
            String code = codeFixe.isBlank()
                    ? String.format("%06d", ThreadLocalRandom.current().nextInt(1_000_000))
                    : codeFixe;
            ReinitMotDePasseEntity e = new ReinitMotDePasseEntity();
            e.setId(UUID.randomUUID());
            e.setIdentifiant(identifiant);
            e.setCodeHache(encoder.encode(code));
            e.setExpireLe(Instant.now().plus(ttl));
            e.setConsomme(false);
            codes.save(e);
            // Canal simulé : destinataire numérique obligatoire (EF-11). L'identifiant peut être un e-mail.
            gateway.envoyer(new MessageSortant(
                    CanalMessage.SMS,
                    "237600000000",
                    "AquaSensus code: " + code + " (expire 15 min). Aucun volume d'eau.",
                    null));
        }
    }

    @Transactional
    public void confirmer(String identifiant, String code, String nouveau) {
        var liste = codes.findByIdentifiantOrderByExpireLeDesc(identifiant);
        var candidat = liste.stream()
                .filter(c -> !c.isConsomme() && Instant.now().isBefore(c.getExpireLe()))
                .filter(c -> encoder.matches(code, c.getCodeHache()))
                .findFirst()
                .orElseThrow(IdentifiantsInvalidesException::new);
        var user = utilisateurs.parIdentifiant(identifiant).orElseThrow(IdentifiantsInvalidesException::new);
        user.definirMotDePasseHache(encoder.encode(nouveau), false);
        utilisateurs.enregistrer(user);
        candidat.setConsomme(true);
        codes.save(candidat);
    }
}
