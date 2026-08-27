package org.aquasensus.identity.application;

import java.util.UUID;
import org.aquasensus.identity.domain.CodeRole;
import org.aquasensus.identity.domain.Utilisateur;
import org.aquasensus.shared.error.AccesRefuseException;
import org.springframework.stereotype.Component;

/**
 * Contrôle rôle + périmètre côté serveur (ENF-22, ISS-005). Les fronts ne protègent rien.
 */
@Component
public class PolitiqueAcces {

    public void exigerComite(Utilisateur utilisateur, UUID comiteId) {
        if (utilisateur.possede(CodeRole.ADMIN)) {
            return;
        }
        if (utilisateur.comitesPerimetre().contains(comiteId)) {
            return;
        }
        throw new AccesRefuseException();
    }
}
