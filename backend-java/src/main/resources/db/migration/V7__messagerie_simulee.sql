-- ISS-047 à ISS-050 : journal SMS/USSD simulé. Téléphone fictif, jamais d'opérateur réel.

CREATE TABLE message_simule (
    id               UUID PRIMARY KEY,
    direction        VARCHAR(16)  NOT NULL,
    canal            VARCHAR(8)   NOT NULL,
    numero_fictif    VARCHAR(32)  NOT NULL,
    numero_hache     VARCHAR(64)  NOT NULL,
    contenu          VARCHAR(320) NOT NULL,
    session_id       UUID,
    signalement_id   UUID,
    traite_le        TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT chk_msg_direction CHECK (direction IN ('ENTRANT', 'SORTANT')),
    CONSTRAINT chk_msg_canal CHECK (canal IN ('SMS', 'USSD'))
);

CREATE INDEX idx_message_simule_traite ON message_simule (traite_le DESC);
