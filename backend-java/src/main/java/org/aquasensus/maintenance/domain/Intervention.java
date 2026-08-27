package org.aquasensus.maintenance.domain;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import org.aquasensus.shared.domain.Agregat;
import org.aquasensus.shared.error.RegleMetierException;

public class Intervention extends Agregat {

    private final String reference;
    private final UUID pointEauId;
    private final TypeIntervention type;
    private final OrigineIntervention origine;
    private UUID technicienId;
    private StatutIntervention statut;
    private LocalDate echeanceSouhaitee;
    private MotifSuspension motifSuspension;
    private String motifAnnulation;
    private CompteRendu compteRendu;
    private Instant ouverteLe;
    private Instant affecteeLe;
    private Instant demarreeLe;
    private Instant realiseeLe;
    private Instant clotureeLe;
    private Integer tempsRetablissementMinutes;
    private UUID confirmeeParId;
    private int version;
    private final Set<UUID> signalementIds;
    private final List<PieceRemplacee> pieces;
    private UUID interventionOrigineId;

    public Intervention(
            UUID id,
            String reference,
            UUID pointEauId,
            TypeIntervention type,
            OrigineIntervention origine,
            UUID technicienId,
            StatutIntervention statut,
            LocalDate echeanceSouhaitee,
            MotifSuspension motifSuspension,
            String motifAnnulation,
            CompteRendu compteRendu,
            Instant ouverteLe,
            Instant affecteeLe,
            Instant demarreeLe,
            Instant realiseeLe,
            Instant clotureeLe,
            Integer tempsRetablissementMinutes,
            UUID confirmeeParId,
            int version,
            Set<UUID> signalementIds,
            List<PieceRemplacee> pieces) {
        super(id);
        this.reference = Objects.requireNonNull(reference);
        this.pointEauId = Objects.requireNonNull(pointEauId);
        this.type = Objects.requireNonNull(type);
        this.origine = Objects.requireNonNull(origine);
        this.technicienId = technicienId;
        this.statut = Objects.requireNonNull(statut);
        this.echeanceSouhaitee = echeanceSouhaitee;
        this.motifSuspension = motifSuspension;
        this.motifAnnulation = motifAnnulation;
        this.compteRendu = compteRendu;
        this.ouverteLe = Objects.requireNonNull(ouverteLe);
        this.affecteeLe = affecteeLe;
        this.demarreeLe = demarreeLe;
        this.realiseeLe = realiseeLe;
        this.clotureeLe = clotureeLe;
        this.tempsRetablissementMinutes = tempsRetablissementMinutes;
        this.confirmeeParId = confirmeeParId;
        this.version = version;
        this.signalementIds = new HashSet<>(signalementIds);
        this.pieces = new ArrayList<>(pieces);
        this.interventionOrigineId = null;
    }

    public static Intervention ouvrir(
            String reference,
            UUID pointEauId,
            TypeIntervention type,
            OrigineIntervention origine,
            Set<UUID> signalements) {
        return new Intervention(
                UUID.randomUUID(),
                reference,
                pointEauId,
                type,
                origine,
                null,
                StatutIntervention.OUVERTE,
                null,
                null,
                null,
                null,
                Instant.now(),
                null,
                null,
                null,
                null,
                null,
                null,
                0,
                signalements,
                List.of());
    }

    public void rattacherOrigine(UUID origineId) {
        this.interventionOrigineId = origineId;
    }

    public static Intervention reouvrirSiRecidive(String reference, Intervention cloturee, Instant maintenant) {
        if (cloturee.statut != StatutIntervention.CLOTUREE || cloturee.clotureeLe == null) {
            throw new RegleMetierException("EF-28", "Seule une intervention clôturée peut être rouverte.");
        }
        if (cloturee.clotureeLe.isBefore(maintenant.minus(Duration.ofDays(15)))) {
            throw new RegleMetierException("EF-28", "La fenêtre de récidive de 15 jours est dépassée.");
        }
        Intervention n = ouvrir(
                reference, cloturee.pointEauId, cloturee.type, OrigineIntervention.MANUELLE, Set.of());
        n.rattacherOrigine(cloturee.id());
        return n;
    }

    public void affecter(UUID technicienId, LocalDate echeance) {
        statut.garantirVers(StatutIntervention.AFFECTEE);
        this.technicienId = Objects.requireNonNull(technicienId);
        this.echeanceSouhaitee = echeance;
        this.statut = StatutIntervention.AFFECTEE;
        this.affecteeLe = Instant.now();
    }

    public void demarrer() {
        statut.garantirVers(StatutIntervention.EN_COURS);
        this.statut = StatutIntervention.EN_COURS;
        this.demarreeLe = Instant.now();
        this.motifSuspension = null;
    }

    public void suspendre(MotifSuspension motif) {
        statut.garantirVers(StatutIntervention.SUSPENDUE);
        if (motif == null) {
            throw new RegleMetierException("EF-22", "La suspension exige un motif fermé.");
        }
        this.motifSuspension = motif;
        this.statut = StatutIntervention.SUSPENDUE;
    }

    public void reprendre() {
        statut.garantirVers(StatutIntervention.EN_COURS);
        this.statut = StatutIntervention.EN_COURS;
        this.motifSuspension = null;
    }

    public void declarerRealisee(CompteRendu compteRendu) {
        statut.garantirVers(StatutIntervention.REALISEE);
        if (compteRendu == null || !compteRendu.estComplet()) {
            throw new RegleMetierException(
                    "RG-05", "Le diagnostic et les actions sont obligatoires pour déclarer l'intervention réalisée.");
        }
        this.compteRendu = compteRendu;
        this.statut = StatutIntervention.REALISEE;
        this.realiseeLe = Instant.now();
    }

    public void enregistrerCompteRendu(CompteRendu compteRendu) {
        this.compteRendu = compteRendu;
    }

    public void ajouterPiece(PieceRemplacee piece) {
        this.pieces.add(piece);
    }

    public DureeRetablissement cloturer(UUID confirmateurId, UUID technicienIdAttendu, UUID declarantId, Instant debutMesure) {
        statut.garantirVers(StatutIntervention.CLOTUREE);
        if (confirmateurId.equals(technicienIdAttendu) || confirmateurId.equals(this.technicienId)) {
            throw new RegleMetierException(
                    "RG-04", "Le rétablissement doit être confirmé par un tiers, pas par le technicien.");
        }
        if (declarantId != null && confirmateurId.equals(declarantId)) {
            throw new RegleMetierException(
                    "RG-04", "Le déclarant du signalement ne peut pas confirmer seul le rétablissement.");
        }
        Instant fin = Instant.now();
        Instant debut = debutMesure != null ? debutMesure : ouverteLe;
        DureeRetablissement duree = DureeRetablissement.entre(debut, fin);
        this.statut = StatutIntervention.CLOTUREE;
        this.clotureeLe = fin;
        this.confirmeeParId = confirmateurId;
        this.tempsRetablissementMinutes = duree.minutes();
        return duree;
    }

    public void renvoyerEnCours() {
        statut.garantirVers(StatutIntervention.EN_COURS);
        this.statut = StatutIntervention.EN_COURS;
        this.realiseeLe = null;
    }

    public void annuler(String motif) {
        statut.garantirVers(StatutIntervention.ANNULEE);
        if (motif == null || motif.isBlank()) {
            throw new RegleMetierException("EF-22", "L'annulation exige un motif.");
        }
        this.motifAnnulation = motif;
        this.statut = StatutIntervention.ANNULEE;
    }

    public void synchroniserVersion(int versionPersistee) {
        this.version = versionPersistee;
    }

    public void garantirVersion(int versionAttendue) {
        if (this.version != versionAttendue) {
            throw new org.aquasensus.shared.error.ConflitException(
                    "L'intervention a été modifiée entre-temps. Rechargez puis réessayez.");
        }
    }

    public String reference() {
        return reference;
    }

    public UUID pointEauId() {
        return pointEauId;
    }

    public TypeIntervention type() {
        return type;
    }

    public OrigineIntervention origine() {
        return origine;
    }

    public UUID technicienId() {
        return technicienId;
    }

    public StatutIntervention statut() {
        return statut;
    }

    public LocalDate echeanceSouhaitee() {
        return echeanceSouhaitee;
    }

    public MotifSuspension motifSuspension() {
        return motifSuspension;
    }

    public CompteRendu compteRendu() {
        return compteRendu;
    }

    public Instant ouverteLe() {
        return ouverteLe;
    }

    public Instant affecteeLe() {
        return affecteeLe;
    }

    public Instant clotureeLe() {
        return clotureeLe;
    }

    public Integer tempsRetablissementMinutes() {
        return tempsRetablissementMinutes;
    }

    public UUID confirmeeParId() {
        return confirmeeParId;
    }

    public int version() {
        return version;
    }

    public Set<UUID> signalementIds() {
        return Collections.unmodifiableSet(signalementIds);
    }

    public List<PieceRemplacee> pieces() {
        return Collections.unmodifiableList(pieces);
    }

    public Instant demarreeLe() {
        return demarreeLe;
    }

    public Instant realiseeLe() {
        return realiseeLe;
    }

    public String motifAnnulation() {
        return motifAnnulation;
    }

    public BigDecimal coutPieces() {
        return pieces.stream()
                .map(p -> p.coutUnitaire() == null
                        ? BigDecimal.ZERO
                        : p.coutUnitaire().multiply(BigDecimal.valueOf(p.quantite())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public UUID interventionOrigineId() {
        return interventionOrigineId;
    }
}
