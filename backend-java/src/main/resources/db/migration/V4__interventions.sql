-- ISS-023 à ISS-028 : interventions, pièces, rattachement aux signalements.
-- Aucune colonne de volume (H-2).

CREATE SEQUENCE intervention_ref_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE intervention (
    id                              UUID PRIMARY KEY,
    reference                       VARCHAR(24)  NOT NULL UNIQUE,
    point_eau_id                    UUID         NOT NULL REFERENCES point_eau (id),
    type                            VARCHAR(24)  NOT NULL,
    origine                         VARCHAR(24)  NOT NULL,
    alerte_id                       UUID,
    technicien_id                   UUID REFERENCES utilisateur (id),
    statut                          VARCHAR(24)  NOT NULL,
    echeance_souhaitee              DATE,
    motif_suspension                VARCHAR(32),
    motif_annulation                VARCHAR(500),
    diagnostic                      VARCHAR(2000),
    cause_racine                    VARCHAR(2000),
    actions                         VARCHAR(2000),
    cout_pieces                     NUMERIC(12, 2),
    cout_main_oeuvre                NUMERIC(12, 2),
    ouverte_le                      TIMESTAMP WITH TIME ZONE NOT NULL,
    affectee_le                     TIMESTAMP WITH TIME ZONE,
    demarree_le                     TIMESTAMP WITH TIME ZONE,
    realisee_le                     TIMESTAMP WITH TIME ZONE,
    cloturee_le                     TIMESTAMP WITH TIME ZONE,
    temps_retablissement_minutes    INTEGER,
    confirmee_par_id                UUID REFERENCES utilisateur (id),
    intervention_origine_id         UUID REFERENCES intervention (id),
    version                         INTEGER      NOT NULL DEFAULT 0,
    CONSTRAINT chk_intervention_type CHECK (type IN ('CORRECTIVE', 'PREVENTIVE', 'INSPECTION')),
    CONSTRAINT chk_intervention_origine CHECK (origine IN ('SIGNALEMENT', 'ALERTE', 'MANUELLE')),
    CONSTRAINT chk_intervention_statut CHECK (statut IN (
        'OUVERTE', 'AFFECTEE', 'EN_COURS', 'SUSPENDUE', 'REALISEE', 'CLOTUREE', 'ANNULEE'))
);

CREATE INDEX idx_intervention_point_statut ON intervention (point_eau_id, statut);
CREATE INDEX idx_intervention_technicien ON intervention (technicien_id, statut);

CREATE TABLE intervention_signalement (
    intervention_id  UUID NOT NULL REFERENCES intervention (id),
    signalement_id   UUID NOT NULL REFERENCES signalement (id),
    PRIMARY KEY (intervention_id, signalement_id)
);

CREATE TABLE piece_remplacee (
    id                UUID PRIMARY KEY,
    intervention_id   UUID         NOT NULL REFERENCES intervention (id),
    reference_piece   VARCHAR(80)  NOT NULL,
    libelle           VARCHAR(160) NOT NULL,
    quantite          INTEGER      NOT NULL,
    cout_unitaire     NUMERIC(12, 2),
    CONSTRAINT chk_piece_quantite CHECK (quantite > 0)
);
