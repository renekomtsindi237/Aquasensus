-- ISS-035 à ISS-042 : indices de santé et alertes. Aucun volume (H-2).

CREATE TABLE indice_sante (
    id                          UUID PRIMARY KEY,
    point_eau_id                UUID         NOT NULL REFERENCES point_eau (id),
    date_calcul                 DATE         NOT NULL,
    score                       NUMERIC(5, 2) NOT NULL,
    bande                       VARCHAR(24)  NOT NULL,
    confiance                   VARCHAR(16)  NOT NULL,
    charge_cumulee_jours        NUMERIC(10, 2),
    intervalle_effectif_jours   INTEGER,
    indicateur_m                NUMERIC(6, 4),
    indicateur_p                NUMERIC(8, 4),
    indicateur_s                NUMERIC(8, 4),
    indicateur_t                NUMERIC(8, 4),
    facteurs                    VARCHAR(4000) NOT NULL,
    version_parametrage         VARCHAR(32)  NOT NULL,
    CONSTRAINT uq_indice_ouvrage_date UNIQUE (point_eau_id, date_calcul),
    CONSTRAINT chk_indice_bande CHECK (bande IN (
        'OPERATIONNEL', 'SOUS_SURVEILLANCE', 'RISQUE_ELEVE', 'CRITIQUE')),
    CONSTRAINT chk_indice_confiance CHECK (confiance IN ('HAUTE', 'MOYENNE', 'FAIBLE'))
);

CREATE INDEX idx_indice_sante_point_date ON indice_sante (point_eau_id, date_calcul DESC);

CREATE TABLE alerte (
    id                   UUID PRIMARY KEY,
    point_eau_id         UUID         NOT NULL REFERENCES point_eau (id),
    type_regle           VARCHAR(40)  NOT NULL,
    niveau               VARCHAR(16)  NOT NULL,
    horizon_jours        INTEGER      NOT NULL,
    emise_le             TIMESTAMP WITH TIME ZONE NOT NULL,
    explication          VARCHAR(2000) NOT NULL,
    recommandation       VARCHAR(500)  NOT NULL,
    facteurs             VARCHAR(4000) NOT NULL,
    statut               VARCHAR(16)  NOT NULL,
    motif_contestation   VARCHAR(500),
    reporter_jusqua      DATE,
    issue                VARCHAR(24),
    version_parametrage  VARCHAR(32)  NOT NULL,
    CONSTRAINT chk_alerte_niveau CHECK (niveau IN ('MODERE', 'ELEVE', 'CRITIQUE')),
    CONSTRAINT chk_alerte_statut CHECK (statut IN (
        'ACTIVE', 'ACQUITTEE', 'REPORTEE', 'TRAITEE', 'CONTESTEE', 'CADUQUE')),
    CONSTRAINT chk_alerte_issue CHECK (issue IS NULL OR issue IN (
        'PANNE_SURVENUE', 'PANNE_EVITEE', 'INDETERMINEE')),
    CONSTRAINT chk_alerte_regle CHECK (type_regle IN (
        'R1_ECHEANCE_MAINTENANCE',
        'R2_DEGRADATION_PROGRESSIVE',
        'R3_FRAGILITE_CHRONIQUE',
        'R4_PRESSION_SAISONNIERE',
        'R5_CUMUL_CRITIQUE'))
);

CREATE INDEX idx_alerte_point_statut ON alerte (point_eau_id, statut);
CREATE INDEX idx_alerte_statut ON alerte (statut);
