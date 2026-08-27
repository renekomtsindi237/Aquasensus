-- ISS-054 / ISS-055 : codes de réinitialisation (SMS simulé). Pas de volume.

CREATE TABLE reinit_mot_de_passe (
    id            UUID PRIMARY KEY,
    identifiant   VARCHAR(120) NOT NULL,
    code_hache    VARCHAR(120) NOT NULL,
    expire_le     TIMESTAMP WITH TIME ZONE NOT NULL,
    consomme      BOOLEAN NOT NULL DEFAULT FALSE,
    cree_le       TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_reinit_identifiant ON reinit_mot_de_passe (identifiant, expire_le DESC);
