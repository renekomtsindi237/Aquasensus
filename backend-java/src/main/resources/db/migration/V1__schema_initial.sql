-- ISS-003 / MD-1 : schéma initial localités, comités, identité (SQL Flyway uniquement).
-- Vocabulaire métier français (cahier de conception §5.1, §16).

CREATE TABLE localite (
    id          UUID PRIMARY KEY,
    code        VARCHAR(32)  NOT NULL UNIQUE,
    nom         VARCHAR(160) NOT NULL,
    niveau      VARCHAR(24)  NOT NULL,
    parent_id   UUID REFERENCES localite (id),
    CONSTRAINT chk_localite_niveau CHECK (niveau IN ('REGION', 'COMMUNE', 'QUARTIER'))
);

CREATE TABLE comite (
    id           UUID PRIMARY KEY,
    nom          VARCHAR(160) NOT NULL,
    localite_id  UUID         NOT NULL REFERENCES localite (id),
    actif        BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE role (
    code        VARCHAR(32) PRIMARY KEY,
    libelle     VARCHAR(80) NOT NULL
);

INSERT INTO role (code, libelle) VALUES
    ('USAGER', 'Habitant / usager'),
    ('DELEGUE', 'Délégué de comité'),
    ('TECHNICIEN', 'Technicien'),
    ('PARTENAIRE', 'Partenaire ONG / mairie'),
    ('ADMIN', 'Administrateur');

CREATE TABLE utilisateur (
    id                          UUID PRIMARY KEY,
    identifiant                 VARCHAR(120) NOT NULL UNIQUE,
    mot_de_passe_hache          VARCHAR(120) NOT NULL,
    nom_affichage               VARCHAR(120) NOT NULL,
    statut                      VARCHAR(24)  NOT NULL,
    echecs_consecutifs          INTEGER      NOT NULL DEFAULT 0,
    verrouille_jusqua           TIMESTAMP WITH TIME ZONE,
    doit_changer_mot_de_passe   BOOLEAN      NOT NULL DEFAULT FALSE,
    cree_le                     TIMESTAMP WITH TIME ZONE  NOT NULL DEFAULT NOW(),
    modifie_le                  TIMESTAMP WITH TIME ZONE  NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_utilisateur_statut CHECK (statut IN ('ACTIF', 'SUSPENDU', 'VERROUILLE'))
);

CREATE TABLE utilisateur_role (
    utilisateur_id  UUID        NOT NULL REFERENCES utilisateur (id),
    role_code       VARCHAR(32) NOT NULL REFERENCES role (code),
    PRIMARY KEY (utilisateur_id, role_code)
);

CREATE TABLE utilisateur_perimetre (
    utilisateur_id  UUID NOT NULL REFERENCES utilisateur (id),
    comite_id       UUID NOT NULL REFERENCES comite (id),
    PRIMARY KEY (utilisateur_id, comite_id)
);

CREATE TABLE session_rafraichissement (
    id              UUID PRIMARY KEY,
    utilisateur_id  UUID         NOT NULL REFERENCES utilisateur (id),
    jeton_hache     VARCHAR(128) NOT NULL UNIQUE,
    expire_le       TIMESTAMP WITH TIME ZONE  NOT NULL,
    revoquee        BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_session_utilisateur ON session_rafraichissement (utilisateur_id);
