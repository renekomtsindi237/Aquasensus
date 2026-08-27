package org.aquasensus.identity.application;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.aquasensus.audit.application.JournalAuditService;
import org.aquasensus.identity.domain.CodeRole;
import org.aquasensus.identity.domain.StatutCompte;
import org.aquasensus.identity.domain.Utilisateur;
import org.aquasensus.identity.domain.UtilisateurRepository;
import org.aquasensus.identity.infrastructure.SessionRafraichissementJpa;
import org.aquasensus.shared.error.ConflitException;
import org.aquasensus.shared.error.RessourceIntrouvableException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CompteService {

    private final UtilisateurRepository utilisateurs;
    private final PasswordEncoder encoder;
    private final SessionRafraichissementJpa sessions;
    private final JournalAuditService audit;

    public CompteService(
            UtilisateurRepository utilisateurs,
            PasswordEncoder encoder,
            SessionRafraichissementJpa sessions,
            JournalAuditService audit) {
        this.utilisateurs = utilisateurs;
        this.encoder = encoder;
        this.sessions = sessions;
        this.audit = audit;
    }

    @Transactional(readOnly = true)
    public List<Utilisateur> lister() {
        return utilisateurs.lister();
    }

    @Transactional
    public Utilisateur creer(String identifiant, String nom, String motDePasseTemporaire, Set<CodeRole> roles, Set<UUID> comites) {
        utilisateurs.parIdentifiant(identifiant).ifPresent(u -> {
            throw new ConflitException("Un compte existe déjà pour cet identifiant.");
        });
        Utilisateur u = Utilisateur.nouveau(
                identifiant, encoder.encode(motDePasseTemporaire), nom, roles, comites, true);
        Utilisateur sauve = utilisateurs.enregistrer(u);
        audit.enregistrer(sauve.id(), "CREATION", "UTILISATEUR", sauve.id().toString(), null, identifiant);
        return sauve;
    }

    @Transactional
    public Utilisateur patcher(UUID id, StatutCompte statut) {
        Utilisateur u = utilisateurs.parId(id).orElseThrow(RessourceIntrouvableException::new);
        if (statut == StatutCompte.SUSPENDU) {
            u.suspendre();
            sessions.findByUtilisateurId(id).forEach(s -> {
                s.setRevoquee(true);
                sessions.save(s);
            });
        } else if (statut == StatutCompte.ACTIF) {
            u.reactiver();
        }
        Utilisateur sauve = utilisateurs.enregistrer(u);
        audit.enregistrer(id, "STATUT", "UTILISATEUR", id.toString(), null, statut.name());
        return sauve;
    }

    @Transactional
    public void changerMotDePasse(UUID id, String actuel, String nouveau) {
        Utilisateur u = utilisateurs.parId(id).orElseThrow(RessourceIntrouvableException::new);
        if (!encoder.matches(actuel, u.motDePasseHache())) {
            throw new org.aquasensus.shared.error.IdentifiantsInvalidesException();
        }
        u.definirMotDePasseHache(encoder.encode(nouveau), false);
        utilisateurs.enregistrer(u);
    }
}
