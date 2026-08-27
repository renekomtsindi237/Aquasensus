package org.aquasensus.prediction.domain;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import org.aquasensus.shared.error.RegleMetierException;

public class Alerte {

    private final UUID id;
    private final UUID pointEauId;
    private final TypeRegleAlerte typeRegle;
    private final NiveauAlerte niveau;
    private final int horizonJours;
    private final Instant emiseLe;
    private final String explication;
    private final String recommandation;
    private final String facteurs;
    private StatutAlerte statut;
    private String motifContestation;
    private LocalDate reporterJusqua;
    private IssueAlerte issue;
    private final String versionParametrage;

    public Alerte(
            UUID id,
            UUID pointEauId,
            TypeRegleAlerte typeRegle,
            NiveauAlerte niveau,
            int horizonJours,
            Instant emiseLe,
            String explication,
            String recommandation,
            String facteurs,
            StatutAlerte statut,
            String motifContestation,
            LocalDate reporterJusqua,
            IssueAlerte issue,
            String versionParametrage) {
        this.id = id;
        this.pointEauId = pointEauId;
        this.typeRegle = typeRegle;
        this.niveau = niveau;
        this.horizonJours = horizonJours;
        this.emiseLe = emiseLe;
        this.explication = explication;
        this.recommandation = recommandation;
        this.facteurs = facteurs;
        this.statut = statut;
        this.motifContestation = motifContestation;
        this.reporterJusqua = reporterJusqua;
        this.issue = issue;
        this.versionParametrage = versionParametrage;
    }

    public static Alerte emettre(
            UUID pointEauId,
            TypeRegleAlerte type,
            NiveauAlerte niveau,
            int horizon,
            String explication,
            String recommandation,
            String facteurs,
            String version) {
        return new Alerte(
                UUID.randomUUID(),
                pointEauId,
                type,
                niveau,
                horizon,
                Instant.now(),
                explication,
                recommandation,
                facteurs,
                StatutAlerte.ACTIVE,
                null,
                null,
                null,
                version);
    }

    public void transiter(StatutAlerte cible, String motif, LocalDate report) {
        if (statut == StatutAlerte.CONTESTEE && cible != StatutAlerte.CONTESTEE) {
            throw new RegleMetierException("RG-07", "Une alerte contestée reste historisée ; elle n'est pas effacée.");
        }
        if (cible == StatutAlerte.CONTESTEE && (motif == null || motif.isBlank())) {
            throw new RegleMetierException("EF-44", "La contestation exige un motif.");
        }
        if (cible == StatutAlerte.REPORTEE && report == null) {
            throw new RegleMetierException("EF-44", "Le report exige une échéance.");
        }
        this.statut = cible;
        if (cible == StatutAlerte.CONTESTEE) {
            this.motifContestation = motif;
        }
        if (cible == StatutAlerte.REPORTEE) {
            this.reporterJusqua = report;
        }
    }

    public void enregistrerIssue(IssueAlerte issue) {
        this.issue = issue;
        if (issue != null && statut == StatutAlerte.ACTIVE) {
            this.statut = StatutAlerte.TRAITEE;
        }
    }

    public void rendreCaduque() {
        if (statut == StatutAlerte.ACTIVE || statut == StatutAlerte.REPORTEE) {
            this.statut = StatutAlerte.CADUQUE;
            if (this.issue == null) {
                this.issue = IssueAlerte.INDETERMINEE;
            }
        }
    }

    public UUID id() {
        return id;
    }

    public UUID pointEauId() {
        return pointEauId;
    }

    public TypeRegleAlerte typeRegle() {
        return typeRegle;
    }

    public NiveauAlerte niveau() {
        return niveau;
    }

    public int horizonJours() {
        return horizonJours;
    }

    public Instant emiseLe() {
        return emiseLe;
    }

    public String explication() {
        return explication;
    }

    public String recommandation() {
        return recommandation;
    }

    public String facteurs() {
        return facteurs;
    }

    public StatutAlerte statut() {
        return statut;
    }

    public String motifContestation() {
        return motifContestation;
    }

    public LocalDate reporterJusqua() {
        return reporterJusqua;
    }

    public IssueAlerte issue() {
        return issue;
    }

    public String versionParametrage() {
        return versionParametrage;
    }
}
