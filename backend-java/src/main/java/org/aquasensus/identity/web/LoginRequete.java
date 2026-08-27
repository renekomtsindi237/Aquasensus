package org.aquasensus.identity.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequete(
        @NotBlank @Size(max = 120) String identifiant, @NotBlank @Size(max = 120) String motDePasse) {}
