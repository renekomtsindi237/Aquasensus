package org.aquasensus.identity.infrastructure;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.aquasensus.identity.application.EmetteurJetons;
import org.aquasensus.identity.application.IdentiteJeton;
import org.aquasensus.identity.application.JetonAuthentification;
import org.aquasensus.identity.application.LecteurJetons;
import org.aquasensus.identity.domain.CodeRole;
import org.aquasensus.identity.domain.Utilisateur;
import org.aquasensus.identity.domain.UtilisateurRepository;
import org.aquasensus.shared.error.IdentifiantsInvalidesException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider implements EmetteurJetons, LecteurJetons {

    public static final Duration DUREE_ACCES = Duration.ofMinutes(15);
    public static final Duration DUREE_RAFRAICHISSEMENT = Duration.ofDays(7);

    private final SecretKey cle;
    private final SessionRafraichissementJpa sessions;
    private final UtilisateurRepository utilisateurs;
    private final SecureRandom alea = new SecureRandom();

    public JwtTokenProvider(
            @Value("${aquasensus.jwt.secret}") String secret,
            SessionRafraichissementJpa sessions,
            UtilisateurRepository utilisateurs) {
        byte[] octets = secret.getBytes(StandardCharsets.UTF_8);
        if (octets.length < 32) {
            throw new IllegalStateException("aquasensus.jwt.secret doit faire au moins 32 octets (ENF-20).");
        }
        this.cle = Keys.hmacShaKeyFor(octets);
        this.sessions = sessions;
        this.utilisateurs = utilisateurs;
    }

    @Override
    @Transactional
    public JetonAuthentification emettre(Utilisateur utilisateur) {
        String acces = construireAcces(utilisateur);
        String rafraichissement = nouveauRafraichissement(utilisateur.id());
        return new JetonAuthentification(
                acces,
                rafraichissement,
                utilisateur.nomAffichage(),
                utilisateur.roles(),
                utilisateur.doitChangerMotDePasse());
    }

    @Override
    @Transactional
    public JetonAuthentification renouveler(String jetonRafraichissementBrut) {
        String empreinte = hacher(jetonRafraichissementBrut);
        SessionRafraichissementEntity session = sessions
                .findByJetonHache(empreinte)
                .orElseThrow(IdentifiantsInvalidesException::new);
        if (session.isRevoquee() || Instant.now().isAfter(session.getExpireLe())) {
            throw new IdentifiantsInvalidesException();
        }
        session.setRevoquee(true);
        sessions.save(session);
        Utilisateur utilisateur = utilisateurs
                .parId(session.getUtilisateurId())
                .orElseThrow(IdentifiantsInvalidesException::new);
        return emettre(utilisateur);
    }

    @Override
    public IdentiteJeton lireAcces(String jetonAcces) {
        Claims claims = analyser(jetonAcces);
        @SuppressWarnings("unchecked")
        List<String> rolesClaims = claims.get("roles", List.class);
        return new IdentiteJeton(
                UUID.fromString(claims.getSubject()),
                claims.get("identifiant", String.class),
                rolesClaims.stream().map(CodeRole::valueOf).collect(java.util.stream.Collectors.toSet()));
    }

    public Claims analyser(String jetonAcces) {
        return Jwts.parser().verifyWith(cle).build().parseSignedClaims(jetonAcces).getPayload();
    }

    private String construireAcces(Utilisateur utilisateur) {
        Instant maintenant = Instant.now();
        List<String> roles = utilisateur.roles().stream().map(CodeRole::name).toList();
        return Jwts.builder()
                .subject(utilisateur.id().toString())
                .claim("identifiant", utilisateur.identifiant())
                .claim("roles", roles)
                .issuedAt(Date.from(maintenant))
                .expiration(Date.from(maintenant.plus(DUREE_ACCES)))
                .signWith(cle)
                .compact();
    }

    private String nouveauRafraichissement(UUID utilisateurId) {
        byte[] brut = new byte[32];
        alea.nextBytes(brut);
        String jeton = Base64.getUrlEncoder().withoutPadding().encodeToString(brut);
        SessionRafraichissementEntity session = new SessionRafraichissementEntity();
        session.setId(UUID.randomUUID());
        session.setUtilisateurId(utilisateurId);
        session.setJetonHache(hacher(jeton));
        session.setExpireLe(Instant.now().plus(DUREE_RAFRAICHISSEMENT));
        session.setRevoquee(false);
        sessions.save(session);
        return jeton;
    }

    static String hacher(String jeton) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(jeton.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
