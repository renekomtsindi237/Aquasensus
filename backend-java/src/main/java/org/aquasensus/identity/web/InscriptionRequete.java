package org.aquasensus.identity.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Inscription publique usager (sans KYC). Mot de passe ≥ 10 caractères (EF-83). */
public record InscriptionRequete(
        @NotBlank @Size(max = 120) String identifiant,
        @NotBlank @Size(max = 120) String nomAffichage,
        @NotBlank @Size(min = 10, max = 120) String motDePasse) {}
