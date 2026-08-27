package org.aquasensus.registry.application;

import java.util.UUID;
import org.aquasensus.identity.application.PolitiqueAcces;
import org.aquasensus.identity.domain.Utilisateur;
import org.aquasensus.identity.domain.UtilisateurRepository;
import org.aquasensus.registry.infrastructure.ComiteEntity;
import org.aquasensus.registry.infrastructure.ComiteJpa;
import org.aquasensus.shared.error.IdentifiantsInvalidesException;
import org.aquasensus.shared.error.RessourceIntrouvableException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConsultationComiteService {

    private final UtilisateurRepository utilisateurs;
    private final PolitiqueAcces politiqueAcces;
    private final ComiteJpa comites;

    public ConsultationComiteService(
            UtilisateurRepository utilisateurs, PolitiqueAcces politiqueAcces, ComiteJpa comites) {
        this.utilisateurs = utilisateurs;
        this.politiqueAcces = politiqueAcces;
        this.comites = comites;
    }

    @Transactional(readOnly = true)
    public ComiteResume consulter(UUID utilisateurId, UUID comiteId) {
        Utilisateur utilisateur = utilisateurs.parId(utilisateurId).orElseThrow(IdentifiantsInvalidesException::new);
        politiqueAcces.exigerComite(utilisateur, comiteId);
        ComiteEntity comite = comites.findById(comiteId).orElseThrow(RessourceIntrouvableException::new);
        return new ComiteResume(comite.getId(), comite.getNom(), comite.getLocaliteId());
    }

    public record ComiteResume(UUID id, String nom, UUID localiteId) {}
}
