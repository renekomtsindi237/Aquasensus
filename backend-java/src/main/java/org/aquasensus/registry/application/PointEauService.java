package org.aquasensus.registry.application;

import java.util.List;
import java.util.UUID;
import org.aquasensus.audit.application.JournalAuditService;
import org.aquasensus.registry.domain.ComiteRepository;
import org.aquasensus.registry.domain.Coordonnees;
import org.aquasensus.registry.domain.EmpriseGeographique;
import org.aquasensus.registry.domain.FiltrePointEau;
import org.aquasensus.registry.domain.HistoriqueEtat;
import org.aquasensus.registry.domain.Localite;
import org.aquasensus.registry.domain.LocaliteRepository;
import org.aquasensus.registry.domain.PointEau;
import org.aquasensus.registry.domain.PointEauRepository;
import org.aquasensus.shared.error.ConflitException;
import org.aquasensus.shared.error.RessourceIntrouvableException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PointEauService {

    private final PointEauRepository points;
    private final LocaliteRepository localites;
    private final ComiteRepository comites;
    private final EmpriseGeographique emprise;
    private final JournalAuditService audit;

    public PointEauService(
            PointEauRepository points,
            LocaliteRepository localites,
            ComiteRepository comites,
            EmpriseGeographique emprise,
            JournalAuditService audit) {
        this.points = points;
        this.localites = localites;
        this.comites = comites;
        this.emprise = emprise;
        this.audit = audit;
    }

    @Transactional
    public PointEau creer(FichePointEau fiche, UUID auteurId) {
        validerReferentiel(fiche);
        points.parCode(fiche.code()).ifPresent(p -> {
            throw new ConflitException("Un point d'eau porte déjà le code " + fiche.code() + ".");
        });
        Coordonnees position = new Coordonnees(fiche.latitude(), fiche.longitude());
        position.garantirEmprise(emprise);
        PointEau point = PointEau.mettreEnService(
                fiche.code(),
                fiche.nomUsage(),
                fiche.type(),
                position,
                fiche.localiteId(),
                fiche.comiteId(),
                fiche.dateMiseEnService(),
                fiche.profondeurM(),
                fiche.debitNominalLMin(),
                fiche.populationDesservie(),
                fiche.intervalleMaintenanceJours(),
                auteurId);
        PointEau sauve = points.enregistrer(point);
        audit.enregistrer(auteurId, "CREATION", "POINT_EAU", sauve.id().toString(), null, sauve.code());
        return sauve;
    }

    @Transactional
    public PointEau modifier(UUID id, FichePointEau fiche) {
        PointEau point = points.parId(id).orElseThrow(RessourceIntrouvableException::new);
        validerReferentiel(fiche);
        if (!point.code().equals(fiche.code())) {
            throw new org.aquasensus.shared.error.RegleMetierException(
                    "EF-01", "Le code d'un ouvrage n'est pas modifiable.");
        }
        Coordonnees position = new Coordonnees(fiche.latitude(), fiche.longitude());
        position.garantirEmprise(emprise);
        point.appliquerFiche(
                fiche.nomUsage(),
                fiche.type(),
                position,
                fiche.localiteId(),
                fiche.comiteId(),
                fiche.dateMiseEnService(),
                fiche.profondeurM(),
                fiche.debitNominalLMin(),
                fiche.populationDesservie(),
                fiche.intervalleMaintenanceJours());
        return points.enregistrer(point);
    }

    @Transactional
    public PointEau desactiver(UUID id) {
        PointEau point = points.parId(id).orElseThrow(RessourceIntrouvableException::new);
        point.desactiver();
        return points.enregistrer(point);
    }

    @Transactional(readOnly = true)
    public PointEau consulter(UUID id, boolean publicSeulement) {
        PointEau point = points.parId(id).orElseThrow(RessourceIntrouvableException::new);
        if (publicSeulement && !point.actif()) {
            throw new RessourceIntrouvableException();
        }
        return point;
    }

    @Transactional(readOnly = true)
    public List<PointEau> lister(FiltrePointEau filtre) {
        int taille = Math.min(Math.max(filtre.taille(), 1), 100);
        return points.rechercher(new FiltrePointEau(
                filtre.localiteId(),
                filtre.niveauLocalite(),
                filtre.comiteId(),
                filtre.etat(),
                filtre.inclureInactifs(),
                Math.max(filtre.page(), 0),
                taille));
    }

    @Transactional(readOnly = true)
    public List<HistoriqueEtat> historique(UUID id) {
        return consulter(id, false).historique();
    }

    @Transactional(readOnly = true)
    public String cheminLocalite(UUID localiteId) {
        Localite courante = localites.parId(localiteId).orElseThrow(RessourceIntrouvableException::new);
        java.util.ArrayList<Localite> parents = new java.util.ArrayList<>();
        UUID parentId = courante.parentId();
        while (parentId != null) {
            Localite parent = localites.parId(parentId).orElse(null);
            if (parent == null) {
                break;
            }
            parents.add(parent);
            parentId = parent.parentId();
        }
        return courante.cheminAvec(parents);
    }

    private void validerReferentiel(FichePointEau fiche) {
        localites.parId(fiche.localiteId()).orElseThrow(RessourceIntrouvableException::new);
        if (!comites.existe(fiche.comiteId())) {
            throw new RessourceIntrouvableException();
        }
    }
}
