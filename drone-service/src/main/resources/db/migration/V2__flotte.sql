CREATE TABLE flotte (
    id        UUID         NOT NULL PRIMARY KEY,
    client_id VARCHAR(64)  NOT NULL,
    nom       VARCHAR(100) NOT NULL
);