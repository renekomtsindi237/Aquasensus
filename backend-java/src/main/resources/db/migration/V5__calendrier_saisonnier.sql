-- ISS-032 / EF-31 : calendrier saisonnier. Aucun volume (H-2).

CREATE TABLE calendrier_saison (
    id            UUID PRIMARY KEY,
    localite_id   UUID REFERENCES localite (id),
    libelle       VARCHAR(60)   NOT NULL,
    jour_debut    INTEGER       NOT NULL,
    jour_fin      INTEGER       NOT NULL,
    coefficient   NUMERIC(3, 2) NOT NULL,
    actif         BOOLEAN       NOT NULL DEFAULT TRUE,
    CONSTRAINT chk_saison_jours CHECK (
        jour_debut BETWEEN 1 AND 366 AND jour_fin BETWEEN 1 AND 366),
    CONSTRAINT chk_saison_coeff CHECK (coefficient > 0)
);

CREATE INDEX idx_calendrier_saison_localite ON calendrier_saison (localite_id);

-- Défaut Cameroun : grande saison sèche (mi-novembre → mi-mars), k = 1,30.
INSERT INTO calendrier_saison (id, localite_id, libelle, jour_debut, jour_fin, coefficient, actif)
VALUES (
    'c5a50131-0000-4000-8000-000000000001',
    NULL,
    'Grande saison sèche',
    320,
    75,
    1.30,
    TRUE
);
