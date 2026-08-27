package org.aquasensus.reporting.application;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;
import org.aquasensus.identity.application.PolitiqueAcces;
import org.aquasensus.identity.domain.Utilisateur;
import org.aquasensus.identity.domain.UtilisateurRepository;
import org.aquasensus.registry.domain.EtatPointEau;
import org.aquasensus.registry.domain.PointEau;
import org.aquasensus.registry.domain.PointEauRepository;
import org.aquasensus.reporting.domain.CanalSignalement;
import org.aquasensus.reporting.domain.CategorieSymptome;
import org.aquasensus.reporting.domain.Gravite;
import org.aquasensus.reporting.domain.Signalement;
import org.aquasensus.reporting.domain.SignalementRepository;
import org.aquasensus.reporting.domain.StatutSignalement;
import org.aquasensus.shared.error.QuotaDepasseException;
import org.aquasensus.shared.error.RegleMetierException;
import org.aquasensus.shared.error.RessourceIntrouvableException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SignalementService {

    public static final String OTP_SIMULE = "123456";
    public static final int QUOTA_PUBLIC_PAR_HEURE = 5;

    private final SignalementRepository signalements;
    private final PointEauRepository points;
    private final UtilisateurRepository utilisateurs;
    private final PolitiqueAcces politiqueAcces;
    private final ApplicationEventPublisher events;

    public SignalementService(
            SignalementRepository signalements,
            PointEauRepository points,
            UtilisateurRepository utilisateurs,
            PolitiqueAcces politiqueAcces,
            ApplicationEventPublisher events) {
        this.signalements = signalements;
        this.points = points;
        this.utilisateurs = utilisateurs;
        this.politiqueAcces = politiqueAcces;
        this.events = events;
    }

    @Transactional
    public ResultatSignalement declarer(CommandeSignalement cmd) {
        var existant = signalements.parUuidClient(cmd.uuidClient());
        if (existant.isPresent()) {
            Signalement s = existant.get();
            PointEau ouvrage = points.parId(s.pointEauId()).orElseThrow(RessourceIntrouvableException::new);
            return ResultatSignalement.idempotent(s, ouvrage);
        }

        PointEau ouvrage = points.parCode(cmd.codePointEau()).orElseThrow(RessourceIntrouvableException::new);
        if (!ouvrage.actif()) {
            throw new RessourceIntrouvableException();
        }

        String hache = null;
        String suffixe = null;
        if (cmd.utilisateurId() == null) {
            if (cmd.telephone() == null || cmd.telephone().isBlank()) {
                throw new RegleMetierException("EF-11", "Un numéro de téléphone est requis sans compte.");
            }
            boolean canalSimule =
                    cmd.canal() == CanalSignalement.SMS || cmd.canal() == CanalSignalement.USSD;
            if (!canalSimule && !OTP_SIMULE.equals(cmd.codeOtp())) {
                throw new RegleMetierException("EF-11", "Code de confirmation incorrect.");
            }
            String normalise = TelephoneDeclarant.normaliser(cmd.telephone());
            hache = TelephoneDeclarant.hacher(normalise);
            suffixe = TelephoneDeclarant.suffixe(normalise);
            Instant depuis = Instant.now().minusSeconds(3600);
            if (signalements.recentsParTelephoneHache(hache, depuis).size() >= QUOTA_PUBLIC_PAR_HEURE) {
                throw new QuotaDepasseException();
            }
        }

        Instant declareLe = cmd.declareLe() == null ? Instant.now() : cmd.declareLe();
        int annee = declareLe.atZone(ZoneOffset.UTC).getYear();
        String reference = "SIG-%d-%05d".formatted(annee, signalements.prochaineReference());

        Signalement nouveau = Signalement.ouvrir(
                reference,
                cmd.uuidClient(),
                ouvrage.id(),
                cmd.categorie(),
                cmd.gravite(),
                cmd.commentaire(),
                cmd.utilisateurId(),
                hache,
                suffixe,
                cmd.canal() == null ? CanalSignalement.WEB : cmd.canal(),
                declareLe,
                ouvrage.populationDesservie());

        var parent = signalements.incidentOuvert(
                ouvrage.id(), cmd.categorie(), declareLe.minus(Signalement.FENETRE_CORROBORATION));
        Signalement incident = nouveau;
        if (parent.isPresent()) {
            Signalement p = parent.get();
            nouveau.corroborerDepuis(p, ouvrage.populationDesservie());
            signalements.enregistrer(p);
            incident = p;
        }
        signalements.enregistrer(nouveau);
        if (cmd.gravite() == Gravite.HAUTE) {
            events.publishEvent(new SignalementGrave(nouveau.id(), ouvrage.id(), ouvrage.comiteId()));
        }
        basculerSiConfirme(incident, ouvrage, cmd.utilisateurId());
        PointEau aJour = points.parId(ouvrage.id()).orElse(ouvrage);
        return ResultatSignalement.cree(nouveau, incident, aJour);
    }

    @Transactional
    public Signalement qualifier(UUID id, UUID delegueId, StatutSignalement decision, String motif) {
        Signalement s = signalements.parId(id).orElseThrow(RessourceIntrouvableException::new);
        PointEau ouvrage = points.parId(s.pointEauId()).orElseThrow(RessourceIntrouvableException::new);
        Utilisateur delegue = utilisateurs.parId(delegueId).orElseThrow(RessourceIntrouvableException::new);
        politiqueAcces.exigerComite(delegue, ouvrage.comiteId());
        EtatPointEau avant = ouvrage.etat();
        s.qualifier(decision, motif);
        signalements.enregistrer(s);
        if (decision == StatutSignalement.QUALIFIE && s.categorie() == CategorieSymptome.PANNE_TOTALE) {
            basculerEnPanne(ouvrage, delegueId);
        }
        if (decision == StatutSignalement.REJETE && ouvrage.etat() != avant) {
            throw new RegleMetierException("RG-11", "Un rejet ne doit pas modifier l'ouvrage.");
        }
        return s;
    }

    @Transactional
    public Signalement figerPriorite(UUID id, UUID delegueId, int valeur, String motif) {
        Signalement s = signalements.parId(id).orElseThrow(RessourceIntrouvableException::new);
        PointEau ouvrage = points.parId(s.pointEauId()).orElseThrow(RessourceIntrouvableException::new);
        Utilisateur delegue = utilisateurs.parId(delegueId).orElseThrow(RessourceIntrouvableException::new);
        politiqueAcces.exigerComite(delegue, ouvrage.comiteId());
        s.figerPriorite(valeur, motif);
        return signalements.enregistrer(s);
    }

    private void basculerSiConfirme(Signalement incident, PointEau ouvrage, UUID auteurId) {
        if (incident.panneTotaleConfirmee()) {
            basculerEnPanne(ouvrage, auteurId);
        }
    }

    private void basculerEnPanne(PointEau ouvrage, UUID auteurId) {
        if (ouvrage.etat() == EtatPointEau.EN_PANNE
                || ouvrage.etat() == EtatPointEau.EN_REPARATION
                || ouvrage.etat() == EtatPointEau.HORS_SERVICE) {
            return;
        }
        ouvrage.changerEtat(EtatPointEau.EN_PANNE, "Panne totale confirmée (RG-02)", auteurId);
        points.enregistrer(ouvrage);
    }

    public record CommandeSignalement(
            UUID uuidClient,
            String codePointEau,
            CategorieSymptome categorie,
            Gravite gravite,
            String commentaire,
            CanalSignalement canal,
            String telephone,
            String codeOtp,
            Instant declareLe,
            UUID utilisateurId) {}

    public record ResultatSignalement(
            Signalement enregistre,
            Signalement incident,
            PointEau ouvrage,
            boolean rejoue) {

        static ResultatSignalement idempotent(Signalement s, PointEau o) {
            return new ResultatSignalement(s, s, o, true);
        }

        static ResultatSignalement cree(Signalement s, Signalement incident, PointEau o) {
            return new ResultatSignalement(s, incident, o, false);
        }
    }

    public record SignalementGrave(UUID signalementId, UUID pointEauId, UUID comiteId) {}
}
