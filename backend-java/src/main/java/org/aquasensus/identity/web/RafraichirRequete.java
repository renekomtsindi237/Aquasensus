package org.aquasensus.identity.web;

import jakarta.validation.constraints.NotBlank;

public record RafraichirRequete(@NotBlank String jetonRafraichissement) {}
