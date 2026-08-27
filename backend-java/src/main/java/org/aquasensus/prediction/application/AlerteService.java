package org.aquasensus.prediction.application;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.aquasensus.identity.application.PolitiqueAcces;
import org.aquasensus.identity.domain.CodeRole;
import org.aquasensus.identity.domain.Utilisateur;
import org.aquasensus.identity.domain.UtilisateurRepository;
import org.aquasensus.prediction.domain.Alerte;
import org.aquasensus.prediction.domain.AlerteRepository;
import org.aquasensus.prediction.domain.StatutAlerte;
import org.aquasensus.registry.domain.PointEau;
import org.aquasensus.registry.domain.PointEauRepository;
import org.aquasensus.shared.error.RessourceIntrouvableException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AlerteService {

    private final AlerteRepository alertes;
    private final PointEauRepository points;
    private final UtilisateurRepository utilisateurs;
    private final PolitiqueAcces politiqueAcces;

    public AlerteService(
            AlerteRepository alertes,
            PointEauRepository points,
            UtilisateurRepository utilisateurs,
            PolitiqueAcces politiqueAcces) {
        this.alertes = alertes;
        this.points = points;
        this.utilisateurs = utilisateurs;
        this.politiqueAcces = politiqueAcces;
    }

    @Transactional(readOnly = true)
    public Alerte consulter(UUID id, UUID acteurId) {
        Alerte a = alertes.parId(id).orElseThrow(RessourceIntrouvableException::new);
        garantirPerimetre(a.pointEauId(), acteurId);
        return a;
    }

    @Transactional
    public Alerte transiter(UUID id, StatutAlerte cible, String motif, LocalDate report, UUID acteurId) {
        Alerte a = alertes.parId(id).orElseThrow(RessourceIntrouvableException::new);
        garantirPerimetre(a.pointEauId(), acteurId);
        a.transiter(cible, motif, report);
        return alertes.enregistrer(a);
    }

    @Transactional(readOnly = true)
    public List<Alerte> actives(UUID acteurId) {
        Utilisateur u = utilisateurs.parId(acteurId).orElseThrow(RessourceIntrouvableException::new);
        if (u.possede(CodeRole.ADMIN) || u.possede(CodeRole.PARTENAIRE)) {
            return alertes.toutes().stream().filter(a -> a.statut() == StatutAlerte.ACTIVE).toList();
        }
        return alertes.activesPourComites(u.comitesPerimetre());
    }

    private void garantirPerimetre(UUID pointEauId, UUID acteurId) {
        PointEau ouvrage = points.parId(pointEauId).orElseThrow(RessourceIntrouvableException::new);
        Utilisateur acteur = utilisateurs.parId(acteurId).orElseThrow(RessourceIntrouvableException::new);
        if (acteur.possede(CodeRole.ADMIN) || acteur.possede(CodeRole.PARTENAIRE)) {
            return;
        }
        politiqueAcces.exigerComite(acteur, ouvrage.comiteId());
    }
}
