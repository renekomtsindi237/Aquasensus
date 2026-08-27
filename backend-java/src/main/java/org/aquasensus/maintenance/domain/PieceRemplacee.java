package org.aquasensus.maintenance.domain;

import java.math.BigDecimal;
import java.util.UUID;

public record PieceRemplacee(
        UUID id, String reference, String libelle, int quantite, BigDecimal coutUnitaire) {}
