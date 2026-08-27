-- ISS-014, ISS-015, ISS-040, ISS-045, ISS-051 à ISS-057, ISS-029, ISS-056.
-- Insertion seule applicative pour journal_audit (aucun UPDATE/DELETE exposé).
-- Aucune colonne de volume (H-2). Pas de téléphone en clair.

CREATE TABLE journal_audit (
    id           UUID PRIMARY KEY,
    horodatage   TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    acteur_id    UUID,
    action       VARCHAR(64)  NOT NULL,
    entite       VARCHAR(64)  NOT NULL,
    entite_id    VARCHAR(64)  NOT NULL,
    avant        VARCHAR(4000),
    apres        VARCHAR(4000)
);

CREATE INDEX idx_journal_audit_entite ON journal_audit (entite, entite_id, horodatage DESC);
CREATE INDEX idx_journal_audit_acteur ON journal_audit (acteur_id, horodatage DESC);

CREATE TABLE notification (
    id               UUID PRIMARY KEY,
    destinataire_id  UUID         NOT NULL REFERENCES utilisateur (id),
    canal            VARCHAR(16)  NOT NULL,
    evenement        VARCHAR(48)  NOT NULL,
    titre            VARCHAR(160) NOT NULL,
    corps            VARCHAR(480) NOT NULL,
    statut           VARCHAR(16)  NOT NULL,
    cle_dedup        VARCHAR(160) NOT NULL UNIQUE,
    lue              BOOLEAN      NOT NULL DEFAULT FALSE,
    cree_le          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_notif_canal CHECK (canal IN ('IN_APP', 'SMS')),
    CONSTRAINT chk_notif_statut CHECK (statut IN ('EN_ATTENTE', 'ENVOYEE', 'ECHOUEE'))
);

CREATE INDEX idx_notification_destinataire ON notification (destinataire_id, cree_le DESC);

CREATE TABLE parametrage_moteur (
    version     VARCHAR(32) PRIMARY KEY,
    contenu     VARCHAR(4000) NOT NULL,
    actif       BOOLEAN       NOT NULL DEFAULT FALSE,
    cree_le     TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

INSERT INTO parametrage_moteur (version, contenu, actif, cree_le) VALUES (
    'v1',
    '{"horizonJours":14,"seuilR1":0.93,"ponderationPopulation":1}',
    TRUE,
    NOW()
);

CREATE TABLE type_piece (
    code     VARCHAR(32) PRIMARY KEY,
    libelle  VARCHAR(160) NOT NULL,
    actif    BOOLEAN      NOT NULL DEFAULT TRUE
);

INSERT INTO type_piece (code, libelle, actif) VALUES
    ('JOINT', 'Joint / garniture', TRUE),
    ('CLAPET', 'Clapet', TRUE),
    ('CREPINE', 'Crépine', TRUE),
    ('POMPE', 'Élément de pompe', TRUE);

CREATE TABLE photo_fiche (
    id             UUID PRIMARY KEY,
    point_eau_id   UUID         NOT NULL REFERENCES point_eau (id),
    nom_stockage   VARCHAR(80)  NOT NULL,
    type_mime      VARCHAR(32)  NOT NULL,
    taille_octets  INTEGER      NOT NULL,
    auteur_id      UUID,
    cree_le        TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_photo_mime CHECK (type_mime IN ('image/jpeg', 'image/png', 'image/webp')),
    CONSTRAINT chk_photo_taille CHECK (taille_octets > 0 AND taille_octets <= 3145728)
);

CREATE INDEX idx_photo_fiche_point ON photo_fiche (point_eau_id, cree_le DESC);
