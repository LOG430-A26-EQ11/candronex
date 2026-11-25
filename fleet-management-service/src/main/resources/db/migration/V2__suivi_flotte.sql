CREATE TABLE suivi_ordre_flotte (
    ordre_id UUID PRIMARY KEY,
    client_id VARCHAR(64) NOT NULL,
    flotte_id UUID NOT NULL,
    etat_ordre VARCHAR(32) NOT NULL,
    version_projection BIGINT NOT NULL DEFAULT 0,
    mis_a_jour_le TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (client_id, flotte_id) REFERENCES flotte (client_id, id)
);

CREATE TABLE suivi_service_drone (
    ordre_id UUID NOT NULL REFERENCES suivi_ordre_flotte (ordre_id),
    drone_id VARCHAR(64) NOT NULL,
    type_service VARCHAR(32) NOT NULL,
    etat_configuration VARCHAR(32) NOT NULL,
    etat_connectivite VARCHAR(32) NOT NULL,
    erreur TEXT,

    PRIMARY KEY (ordre_id, drone_id, type_service)
);