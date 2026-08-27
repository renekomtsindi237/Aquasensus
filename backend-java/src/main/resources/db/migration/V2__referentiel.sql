-- ISS-010 / ISS-011 / ISS-012 : points d'eau et historique d'état.
-- Aucune colonne de volume (H-2).

CREATE TABLE point_eau (
    id                              UUID PRIMARY KEY,
    code                            VARCHAR(24)  NOT NULL UNIQUE,
    nom_usage                       VARCHAR(120) NOT NULL,
    type                            VARCHAR(32)  NOT NULL,
    latitude                        NUMERIC(9, 6) NOT NULL,
    longitude                       NUMERIC(9, 6) NOT NULL,
    localite_id                     UUID         NOT NULL REFERENCES localite (id),
    comite_id                       UUID         NOT NULL REFERENCES comite (id),
    date_mise_en_service            DATE,
    profondeur_m                    NUMERIC(6, 2),
    debit_nominal_l_min             NUMERIC(8, 2),
    population_desservie            INTEGER,
    intervalle_maintenance_jours    INTEGER,
    etat                            VARCHAR(32)  NOT NULL,
    actif                           BOOLEAN      NOT NULL DEFAULT TRUE,
    version                         INTEGER      NOT NULL DEFAULT 0,
    cree_le                         TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    modifie_le                      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_point_eau_type CHECK (type IN (
        'FORAGE_MANUEL', 'FORAGE_MOTORISE', 'MINI_RESEAU', 'BORNE_FONTAINE')),
    CONSTRAINT chk_point_eau_etat CHECK (etat IN (
        'OPERATIONNEL', 'SOUS_SURVEILLANCE', 'RISQUE_ELEVE',
        'EN_PANNE', 'EN_REPARATION', 'HORS_SERVICE')),
    CONSTRAINT chk_point_eau_population CHECK (population_desservie IS NULL OR population_desservie >= 0)
);

CREATE INDEX idx_point_eau_localite_etat ON point_eau (localite_id, etat);
CREATE INDEX idx_point_eau_comite ON point_eau (comite_id);

CREATE TABLE historique_etat (
    id              UUID PRIMARY KEY,
    point_eau_id    UUID         NOT NULL REFERENCES point_eau (id),
    etat_precedent  VARCHAR(32),
    etat_nouveau    VARCHAR(32)  NOT NULL,
    motif           VARCHAR(500) NOT NULL,
    auteur_id       UUID,
    survenu_le      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_historique_etat_point ON historique_etat (point_eau_id, survenu_le DESC);
