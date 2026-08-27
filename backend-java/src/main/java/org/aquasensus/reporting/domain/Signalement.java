package org.aquasensus.reporting.domain;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import org.aquasensus.shared.domain.Agregat;
import org.aquasensus.shared.error.RegleMetierException;

public class Signalement extends Agregat {

    public static final Duration FENETRE_CORROBORATION = Duration.ofHours(24);

    private final String reference;
    private final UUID uuidClient;
    private final UUID pointEauId;
    private final CategorieSymptome categorie;
    private final Gravite gravite;
    private final String commentaire;
    private final UUID declarantUtilisateurId;
    private final String telephoneHache;
    private final String telephoneSuffixe;
    private final CanalSignalement canal;
    private StatutSignalement statut;
    private UUID signalementParentId;
    private int nbCorroborations;
    private int priorite;
    private boolean prioriteFigee;
    private String motifQualification;
    private final Instant declareLe;

    public Signalement(
            UUID id,
            String reference,
            UUID uuidClient,
            UUID pointEauId,
            CategorieSymptome categorie,
            Gravite gravite,
            String commentaire,
            UUID declarantUtilisateurId,
            String telephoneHache,
            String telephoneSuffixe,
            CanalSignalement canal,
            StatutSignalement statut,
            UUID signalementParentId,
            int nbCorroborations,
            int priorite,
            boolean prioriteFigee,
            String motifQualification,
            Instant declareLe) {
        super(id);
        this.reference = Objects.requireNonNull(reference);
        this.uuidClient = Objects.requireNonNull(uuidClient);
        this.pointEauId = Objects.requireNonNull(pointEauId);
        this.categorie = Objects.requireNonNull(categorie);
        this.gravite = Objects.requireNonNull(gravite);
        this.commentaire = commentaire;
        this.declarantUtilisateurId = declarantUtilisateurId;
        this.telephoneHache = telephoneHache;
        this.telephoneSuffixe = telephoneSuffixe;
        this.canal = Objects.requireNonNull(canal);
        this.statut = Objects.requireNonNull(statut);
        this.signalementParentId = signalementParentId;
        this.nbCorroborations = nbCorroborations;
        this.priorite = priorite;
        this.prioriteFigee = prioriteFigee;
        this.motifQualification = motifQualification;
        this.declareLe = Objects.requireNonNull(declareLe);
    }

    public static Signalement ouvrir(
            String reference,
            UUID uuidClient,
            UUID pointEauId,
            CategorieSymptome categorie,
            Gravite gravite,
            String commentaire,
            UUID declarantUtilisateurId,
            String telephoneHache,
            String telephoneSuffixe,
            CanalSignalement canal,
            Instant declareLe) {
        return ouvrir(
                reference,
                uuidClient,
                pointEauId,
                categorie,
                gravite,
                commentaire,
                declarantUtilisateurId,
                telephoneHache,
                telephoneSuffixe,
                canal,
                declareLe,
                null);
    }

    public static Signalement ouvrir(
            String reference,
            UUID uuidClient,
            UUID pointEauId,
            CategorieSymptome categorie,
            Gravite gravite,
            String commentaire,
            UUID declarantUtilisateurId,
            String telephoneHache,
            String telephoneSuffixe,
            CanalSignalement canal,
            Instant declareLe,
            Integer populationDesservie) {
        return new Signalement(
                UUID.randomUUID(),
                reference,
                uuidClient,
                pointEauId,
                categorie,
                gravite,
                commentaire,
                declarantUtilisateurId,
                telephoneHache,
                telephoneSuffixe,
                canal,
                StatutSignalement.RECU,
                null,
                0,
                prioriteCalculee(gravite, 0, populationDesservie),
                false,
                null,
                declareLe);
    }

    public void corroborerDepuis(Signalement parent) {
        this.signalementParentId = parent.id();
        this.statut = StatutSignalement.DOUBLON;
        parent.ajouterCorroboration(null);
    }

    public void corroborerDepuis(Signalement parent, Integer populationDesservie) {
        this.signalementParentId = parent.id();
        this.statut = StatutSignalement.DOUBLON;
        parent.ajouterCorroboration(populationDesservie);
    }

    public void ajouterCorroboration() {
        ajouterCorroboration(null);
    }

    public void ajouterCorroboration(Integer populationDesservie) {
        nbCorroborations += 1;
        if (!prioriteFigee) {
            priorite = prioriteCalculee(gravite, nbCorroborations, populationDesservie);
        }
    }

    public void figerPriorite(int valeur, String motif) {
        if (motif == null || motif.isBlank()) {
            throw new RegleMetierException("EF-16", "Une justification est obligatoire pour geler la priorité.");
        }
        if (valeur < 1 || valeur > 5) {
            throw new RegleMetierException("EF-16", "La priorité est un entier de 1 (urgent) à 5.");
        }
        this.priorite = valeur;
        this.prioriteFigee = true;
    }

    public boolean dansFenetreDe(Signalement candidat) {
        return pointEauId.equals(candidat.pointEauId)
                && categorie == candidat.categorie
                && !declareLe.isBefore(candidat.declareLe.minus(FENETRE_CORROBORATION));
    }

    public boolean panneTotaleConfirmee() {
        return categorie == CategorieSymptome.PANNE_TOTALE && nbCorroborations >= 2;
    }

    public void marquerResolu() {
        this.statut = StatutSignalement.RESOLU;
    }

    public void qualifier(StatutSignalement decision, String motif) {
        if (decision != StatutSignalement.QUALIFIE
                && decision != StatutSignalement.REJETE
                && decision != StatutSignalement.DOUBLON) {
            throw new RegleMetierException("EF-15", "Décision de qualification inconnue.");
        }
        if (motif == null || motif.isBlank()) {
            throw new RegleMetierException("RG-11", "Un motif est obligatoire pour qualifier ou rejeter.");
        }
        this.statut = decision;
        this.motifQualification = motif;
    }

    public boolean rejetSansEffetSurOuvrage() {
        return statut == StatutSignalement.REJETE;
    }

    static int prioriteCalculee(Gravite gravite, int corroborations, Integer populationDesservie) {
        int base = switch (gravite) {
            case HAUTE -> 1;
            case MOYENNE -> 3;
            case FAIBLE -> 5;
        };
        int p = Math.max(1, Math.min(5, base - corroborations));
        if (populationDesservie != null && populationDesservie >= 400) {
            p = Math.max(1, p - 1);
        }
        return p;
    }

    public String reference() {
        return reference;
    }

    public UUID uuidClient() {
        return uuidClient;
    }

    public UUID pointEauId() {
        return pointEauId;
    }

    public CategorieSymptome categorie() {
        return categorie;
    }

    public Gravite gravite() {
        return gravite;
    }

    public String commentaire() {
        return commentaire;
    }

    public UUID declarantUtilisateurId() {
        return declarantUtilisateurId;
    }

    public String telephoneHache() {
        return telephoneHache;
    }

    public String telephoneSuffixe() {
        return telephoneSuffixe;
    }

    public CanalSignalement canal() {
        return canal;
    }

    public StatutSignalement statut() {
        return statut;
    }

    public UUID signalementParentId() {
        return signalementParentId;
    }

    public int nbCorroborations() {
        return nbCorroborations;
    }

    public int priorite() {
        return priorite;
    }

    public boolean prioriteFigee() {
        return prioriteFigee;
    }

    public Instant declareLe() {
        return declareLe;
    }

    public String motifQualification() {
        return motifQualification;
    }
}
