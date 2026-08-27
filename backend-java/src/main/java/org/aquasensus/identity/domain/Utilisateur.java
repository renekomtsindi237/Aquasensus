package org.aquasensus.identity.domain;

import java.time.Instant;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import org.aquasensus.shared.domain.Agregat;
import org.aquasensus.shared.error.CompteVerrouilleException;
import org.aquasensus.shared.error.RegleMetierException;

/**
 * Agrégat Utilisateur. Le hachage du mot de passe reste hors de ce type (infrastructure).
 * ISS-004, EF-85.
 */
public class Utilisateur extends Agregat {

    public static final int SEUIL_VERROUILLAGE = 5;
    public static final int DUREE_VERROUILLAGE_MINUTES = 15;

    private final String identifiant;
    private String motDePasseHache;
    private String nomAffichage;
    private StatutCompte statut;
    private int echecsConsecutifs;
    private Instant verrouilleJusqua;
    private boolean doitChangerMotDePasse;
    private final Set<CodeRole> roles;
    private final Set<UUID> comitesPerimetre;

    public Utilisateur(
            UUID id,
            String identifiant,
            String motDePasseHache,
            String nomAffichage,
            StatutCompte statut,
            int echecsConsecutifs,
            Instant verrouilleJusqua,
            boolean doitChangerMotDePasse,
            Set<CodeRole> roles,
            Set<UUID> comitesPerimetre) {
        super(id);
        this.identifiant = Objects.requireNonNull(identifiant);
        this.motDePasseHache = Objects.requireNonNull(motDePasseHache);
        this.nomAffichage = Objects.requireNonNull(nomAffichage);
        this.statut = Objects.requireNonNull(statut);
        this.echecsConsecutifs = echecsConsecutifs;
        this.verrouilleJusqua = verrouilleJusqua;
        this.doitChangerMotDePasse = doitChangerMotDePasse;
        this.roles = roles.isEmpty() ? EnumSet.noneOf(CodeRole.class) : EnumSet.copyOf(roles);
        this.comitesPerimetre = Set.copyOf(comitesPerimetre);
    }

    public static Utilisateur nouveau(
            String identifiant,
            String motDePasseHache,
            String nomAffichage,
            Set<CodeRole> roles,
            Set<UUID> comitesPerimetre,
            boolean doitChangerMotDePasse) {
        return new Utilisateur(
                UUID.randomUUID(),
                identifiant,
                motDePasseHache,
                nomAffichage,
                StatutCompte.ACTIF,
                0,
                null,
                doitChangerMotDePasse,
                roles,
                comitesPerimetre);
    }

    public void garantirAcces(Instant maintenant) {
        if (statut == StatutCompte.SUSPENDU) {
            throw new RegleMetierException("EF-81", "Ce compte est suspendu.");
        }
        if (estVerrouille(maintenant)) {
            throw new CompteVerrouilleException();
        }
        if (statut == StatutCompte.VERROUILLE && !estVerrouille(maintenant)) {
            statut = StatutCompte.ACTIF;
            echecsConsecutifs = 0;
            verrouilleJusqua = null;
        }
    }

    public boolean estVerrouille(Instant maintenant) {
        return verrouilleJusqua != null && maintenant.isBefore(verrouilleJusqua);
    }

    public void enregistrerEchec(Instant maintenant) {
        echecsConsecutifs += 1;
        if (echecsConsecutifs >= SEUIL_VERROUILLAGE) {
            statut = StatutCompte.VERROUILLE;
            verrouilleJusqua = maintenant.plusSeconds(DUREE_VERROUILLAGE_MINUTES * 60L);
        }
    }

    public void enregistrerSucces() {
        echecsConsecutifs = 0;
        verrouilleJusqua = null;
        if (statut == StatutCompte.VERROUILLE) {
            statut = StatutCompte.ACTIF;
        }
    }

    public boolean possede(CodeRole role) {
        return roles.contains(role);
    }

    public String identifiant() {
        return identifiant;
    }

    public String motDePasseHache() {
        return motDePasseHache;
    }

    public String nomAffichage() {
        return nomAffichage;
    }

    public StatutCompte statut() {
        return statut;
    }

    public int echecsConsecutifs() {
        return echecsConsecutifs;
    }

    public Instant verrouilleJusqua() {
        return verrouilleJusqua;
    }

    public boolean doitChangerMotDePasse() {
        return doitChangerMotDePasse;
    }

    public Set<CodeRole> roles() {
        return Collections.unmodifiableSet(roles);
    }

    public Set<UUID> comitesPerimetre() {
        return comitesPerimetre;
    }

    public void suspendre() {
        this.statut = StatutCompte.SUSPENDU;
    }

    public void reactiver() {
        this.statut = StatutCompte.ACTIF;
    }

    public void definirMotDePasseHache(String hash, boolean doitChanger) {
        this.motDePasseHache = Objects.requireNonNull(hash);
        this.doitChangerMotDePasse = doitChanger;
    }
}
