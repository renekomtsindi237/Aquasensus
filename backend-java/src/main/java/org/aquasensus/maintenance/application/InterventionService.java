package org.aquasensus.maintenance.application;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.aquasensus.identity.application.PolitiqueAcces;
import org.aquasensus.identity.domain.CodeRole;
import org.aquasensus.identity.domain.Utilisateur;
import org.aquasensus.identity.domain.UtilisateurRepository;
import org.aquasensus.maintenance.domain.CompteRendu;
import org.aquasensus.maintenance.domain.Intervention;
import org.aquasensus.maintenance.domain.InterventionRepository;
import org.aquasensus.maintenance.domain.MotifSuspension;
import org.aquasensus.maintenance.domain.OrigineIntervention;
import org.aquasensus.maintenance.domain.PieceRemplacee;
import org.aquasensus.maintenance.domain.StatutIntervention;
import org.aquasensus.maintenance.domain.TypeIntervention;
import org.aquasensus.registry.domain.EtatPointEau;
import org.aquasensus.registry.domain.PointEau;
import org.aquasensus.registry.domain.PointEauRepository;
import org.aquasensus.reporting.domain.Signalement;
import org.aquasensus.reporting.domain.SignalementRepository;
import org.aquasensus.reporting.domain.StatutSignalement;
import org.aquasensus.shared.error.AccesRefuseException;
import org.aquasensus.shared.error.RegleMetierException;
import org.aquasensus.shared.error.RessourceIntrouvableException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InterventionService {

    private final InterventionRepository interventions;
    private final PointEauRepository points;
    private final SignalementRepository signalements;
    private final UtilisateurRepository utilisateurs;
    private final PolitiqueAcces politiqueAcces;
    private final ApplicationEventPublisher events;

    public InterventionService(
            InterventionRepository interventions,
            PointEauRepository points,
            SignalementRepository signalements,
            UtilisateurRepository utilisateurs,
            PolitiqueAcces politiqueAcces,
            ApplicationEventPublisher events) {
        this.interventions = interventions;
        this.points = points;
        this.signalements = signalements;
        this.utilisateurs = utilisateurs;
        this.politiqueAcces = politiqueAcces;
        this.events = events;
    }

    @Transactional
    public Intervention ouvrir(CommandeOuverture cmd, UUID acteurId) {
        PointEau ouvrage = points.parId(cmd.pointEauId()).orElseThrow(RessourceIntrouvableException::new);
        Utilisateur acteur = utilisateurs.parId(acteurId).orElseThrow(RessourceIntrouvableException::new);
        politiqueAcces.exigerComite(acteur, ouvrage.comiteId());
        Set<UUID> rattaches = new HashSet<>();
        if (cmd.origine() == OrigineIntervention.SIGNALEMENT) {
            if (cmd.signalementIds() == null || cmd.signalementIds().isEmpty()) {
                throw new RegleMetierException("EF-20", "Rattachez au moins un signalement qualifié.");
            }
            for (UUID sid : cmd.signalementIds()) {
                Signalement s = signalements.parId(sid).orElseThrow(RessourceIntrouvableException::new);
                if (!s.pointEauId().equals(ouvrage.id())) {
                    throw new RegleMetierException("EF-20", "Le signalement ne concerne pas cet ouvrage.");
                }
                if (s.statut() != StatutSignalement.QUALIFIE) {
                    throw new RegleMetierException("EF-20", "Seuls les signalements qualifiés ouvrent une intervention.");
                }
                rattaches.add(sid);
            }
        }
        int annee = Instant.now().atZone(ZoneOffset.UTC).getYear();
        String reference = "INT-%d-%04d".formatted(annee, interventions.prochaineReference());
        Intervention i = Intervention.ouvrir(
                reference, ouvrage.id(), cmd.type(), cmd.origine(), rattaches);
        return interventions.enregistrer(i);
    }

    @Transactional
    public Intervention reouvrir(UUID id, UUID acteurId) {
        Intervention cloturee = charger(id);
        PointEau ouvrage = points.parId(cloturee.pointEauId()).orElseThrow(RessourceIntrouvableException::new);
        Utilisateur acteur = utilisateurs.parId(acteurId).orElseThrow(RessourceIntrouvableException::new);
        politiqueAcces.exigerComite(acteur, ouvrage.comiteId());
        int annee = Instant.now().atZone(ZoneOffset.UTC).getYear();
        String reference = "INT-%d-%04d".formatted(annee, interventions.prochaineReference());
        Intervention n = Intervention.reouvrirSiRecidive(reference, cloturee, Instant.now());
        return interventions.enregistrer(n);
    }

    @Transactional
    public Intervention affecter(UUID id, UUID technicienId, LocalDate echeance, int version, UUID delegueId) {
        Intervention i = charger(id);
        i.garantirVersion(version);
        PointEau ouvrage = points.parId(i.pointEauId()).orElseThrow(RessourceIntrouvableException::new);
        Utilisateur delegue = utilisateurs.parId(delegueId).orElseThrow(RessourceIntrouvableException::new);
        politiqueAcces.exigerComite(delegue, ouvrage.comiteId());
        Utilisateur tech = utilisateurs.parId(technicienId).orElseThrow(RessourceIntrouvableException::new);
        if (!tech.possede(CodeRole.TECHNICIEN) && !tech.possede(CodeRole.ADMIN)) {
            throw new RegleMetierException("EF-21", "L'utilisateur affecté n'est pas technicien.");
        }
        i.affecter(technicienId, echeance);
        Intervention sauvee = interventions.enregistrer(i);
        events.publishEvent(new TechnicienAffecte(sauvee.id(), technicienId));
        return sauvee;
    }

    @Transactional
    public Intervention transiter(UUID id, CommandeTransition cmd, UUID acteurId) {
        Intervention i = charger(id);
        i.garantirVersion(cmd.version());
        PointEau ouvrage = points.parId(i.pointEauId()).orElseThrow(RessourceIntrouvableException::new);
        Utilisateur acteur = utilisateurs.parId(acteurId).orElseThrow(RessourceIntrouvableException::new);
        garantirActeur(acteur, i, ouvrage, cmd.cible());

        switch (cmd.cible()) {
            case EN_COURS -> {
                if (i.statut() == StatutIntervention.REALISEE) {
                    i.renvoyerEnCours();
                } else if (i.statut() == StatutIntervention.SUSPENDUE) {
                    i.reprendre();
                } else {
                    i.demarrer();
                    if (ouvrage.etat().autoriseVers(EtatPointEau.EN_REPARATION)) {
                        ouvrage.changerEtat(EtatPointEau.EN_REPARATION, "Intervention démarrée", acteurId);
                        points.enregistrer(ouvrage);
                    }
                }
            }
            case SUSPENDUE -> i.suspendre(cmd.motifSuspension());
            case REALISEE -> i.declarerRealisee(cmd.compteRendu());
            case CLOTUREE -> cloturer(i, ouvrage, acteur);
            case ANNULEE -> i.annuler(cmd.motifAnnulation());
            default -> throw new RegleMetierException("EF-22", "Cible de transition non gérée ici.");
        }
        return interventions.enregistrer(i);
    }

    @Transactional
    public Intervention enregistrerCompteRendu(UUID id, CompteRendu cr, UUID acteurId) {
        Intervention i = charger(id);
        garantirTechnicien(acteurId, i);
        i.enregistrerCompteRendu(cr);
        return interventions.enregistrer(i);
    }

    @Transactional
    public Intervention ajouterPiece(UUID id, PieceRemplacee piece, UUID acteurId) {
        Intervention i = charger(id);
        garantirTechnicien(acteurId, i);
        i.ajouterPiece(piece);
        return interventions.enregistrer(i);
    }

    @Transactional(readOnly = true)
    public DossierBriefing briefing(UUID id, UUID acteurId) {
        Intervention i = charger(id);
        PointEau ouvrage = points.parId(i.pointEauId()).orElseThrow(RessourceIntrouvableException::new);
        Utilisateur acteur = utilisateurs.parId(acteurId).orElseThrow(RessourceIntrouvableException::new);
        if (!acteur.possede(CodeRole.ADMIN)
                && !acteur.possede(CodeRole.DELEGUE)
                && !acteurId.equals(i.technicienId())) {
            throw new AccesRefuseException();
        }
        if (acteur.possede(CodeRole.DELEGUE)) {
            politiqueAcces.exigerComite(acteur, ouvrage.comiteId());
        }
        List<Signalement> rattaches = signalements.parIds(i.signalementIds());
        List<String> symptomes = rattaches.stream()
                .map(s -> s.categorie().name() + " ×" + (s.nbCorroborations() + 1))
                .toList();
        List<String> pannes = ouvrage.historique().stream()
                .filter(h -> h.etatNouveau() == EtatPointEau.EN_PANNE)
                .map(h -> h.survenuLe() + " — " + h.motif())
                .toList();
        List<String> pieces = i.pieces().stream()
                .map(p -> p.libelle() + " (" + p.reference() + ")")
                .toList();
        return new DossierBriefing(
                i.reference(),
                ouvrage.code(),
                ouvrage.nomUsage(),
                ouvrage.position().latitude() + ", " + ouvrage.position().longitude(),
                symptomes,
                pannes,
                pieces);
    }

    @Transactional(readOnly = true)
    public Intervention consulter(UUID id) {
        return charger(id);
    }

    private void cloturer(Intervention i, PointEau ouvrage, Utilisateur confirmateur) {
        Instant debut = i.signalementIds().stream()
                .map(signalements::parId)
                .flatMap(java.util.Optional::stream)
                .map(Signalement::declareLe)
                .min(Instant::compareTo)
                .orElse(i.ouverteLe());
        UUID declarant = i.signalementIds().stream()
                .map(signalements::parId)
                .flatMap(java.util.Optional::stream)
                .map(Signalement::declarantUtilisateurId)
                .filter(id -> id != null)
                .findFirst()
                .orElse(null);
        i.cloturer(confirmateur.id(), i.technicienId(), declarant, debut);
        for (UUID sid : i.signalementIds()) {
            signalements.parId(sid).ifPresent(s -> {
                s.marquerResolu();
                signalements.enregistrer(s);
            });
        }
        if (ouvrage.etat() == EtatPointEau.EN_PANNE
                && ouvrage.etat().autoriseVers(EtatPointEau.EN_REPARATION)) {
            ouvrage.changerEtat(EtatPointEau.EN_REPARATION, "Clôture en cours", confirmateur.id());
        }
        if (ouvrage.etat() == EtatPointEau.EN_REPARATION) {
            ouvrage.changerEtat(EtatPointEau.OPERATIONNEL, "Rétablissement confirmé", confirmateur.id());
            points.enregistrer(ouvrage);
        }
        events.publishEvent(new RetablissementConfirme(i.id(), ouvrage.id()));
    }

    private void garantirActeur(
            Utilisateur acteur, Intervention i, PointEau ouvrage, StatutIntervention cible) {
        if (acteur.possede(CodeRole.ADMIN)) {
            return;
        }
        if (cible == StatutIntervention.CLOTUREE || cible == StatutIntervention.ANNULEE
                || (cible == StatutIntervention.EN_COURS && i.statut() == StatutIntervention.REALISEE)
                || cible == StatutIntervention.AFFECTEE) {
            politiqueAcces.exigerComite(acteur, ouvrage.comiteId());
            return;
        }
        if (cible == StatutIntervention.EN_COURS
                || cible == StatutIntervention.SUSPENDUE
                || cible == StatutIntervention.REALISEE) {
            garantirTechnicien(acteur.id(), i);
        }
    }

    private void garantirTechnicien(UUID acteurId, Intervention i) {
        Utilisateur acteur = utilisateurs.parId(acteurId).orElseThrow(RessourceIntrouvableException::new);
        if (acteur.possede(CodeRole.ADMIN)) {
            return;
        }
        if (i.technicienId() == null || !i.technicienId().equals(acteurId)) {
            throw new AccesRefuseException();
        }
    }

    private Intervention charger(UUID id) {
        return interventions.parId(id).orElseThrow(RessourceIntrouvableException::new);
    }

    public record CommandeOuverture(
            UUID pointEauId,
            TypeIntervention type,
            OrigineIntervention origine,
            Set<UUID> signalementIds) {}

    public record CommandeTransition(
            StatutIntervention cible,
            int version,
            MotifSuspension motifSuspension,
            String motifAnnulation,
            CompteRendu compteRendu) {}

    public record DossierBriefing(
            String reference,
            String codeOuvrage,
            String nomUsage,
            String accesGps,
            List<String> symptomesCorrobores,
            List<String> historiquePannes,
            List<String> piecesDejaPosees) {}

    public record TechnicienAffecte(UUID interventionId, UUID technicienId) {}

    public record RetablissementConfirme(UUID interventionId, UUID pointEauId) {}
}
