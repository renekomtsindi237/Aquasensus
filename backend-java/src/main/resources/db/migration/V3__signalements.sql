-- ISS-016 à ISS-022 : signalements, corroboration, idempotence.
-- Aucune colonne de volume (H-2).

CREATE SEQUENCE signalement_ref_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE signalement (
    id                          UUID PRIMARY KEY,
    reference                   VARCHAR(24)  NOT NULL UNIQUE,
    uuid_client                 UUID         NOT NULL UNIQUE,
    point_eau_id                UUID         NOT NULL REFERENCES point_eau (id),
    categorie                   VARCHAR(32)  NOT NULL,
    gravite                     VARCHAR(16)  NOT NULL,
    commentaire                 VARCHAR(500),
    declarant_utilisateur_id    UUID REFERENCES utilisateur (id),
    declarant_telephone_hache   VARCHAR(64),
    declarant_telephone_suffixe VARCHAR(4),
    canal                       VARCHAR(16)  NOT NULL,
    statut                      VARCHAR(16)  NOT NULL,
    signalement_parent_id       UUID REFERENCES signalement (id),
    nb_corroborations           INTEGER      NOT NULL DEFAULT 0,
    priorite                    INTEGER      NOT NULL,
    priorite_figee              BOOLEAN      NOT NULL DEFAULT FALSE,
    motif_qualification         VARCHAR(500),
    declare_le                  TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT chk_signalement_categorie CHECK (categorie IN (
        'PANNE_TOTALE', 'DEBIT_FAIBLE', 'EAU_TROUBLE', 'EAU_MALODORANTE',
        'BRUIT_ANORMAL', 'FUITE', 'DEGRADATION_OUVRAGE', 'ATTENTE_EXCESSIVE', 'AUTRE')),
    CONSTRAINT chk_signalement_gravite CHECK (gravite IN ('FAIBLE', 'MOYENNE', 'HAUTE')),
    CONSTRAINT chk_signalement_canal CHECK (canal IN ('WEB', 'MOBILE', 'SMS', 'USSD')),
    CONSTRAINT chk_signalement_statut CHECK (statut IN (
        'RECU', 'QUALIFIE', 'REJETE', 'DOUBLON', 'RESOLU'))
);

CREATE INDEX idx_signalement_point_date ON signalement (point_eau_id, declare_le DESC);
CREATE INDEX idx_signalement_parent ON signalement (signalement_parent_id);
CREATE INDEX idx_signalement_tel_hache ON signalement (declarant_telephone_hache, declare_le);
