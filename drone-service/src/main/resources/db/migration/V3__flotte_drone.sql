ALTER TABLE flotte
    ADD CONSTRAINT uq_flotte_client_id UNIQUE (client_id, id);

CREATE TABLE flotte_drone (
    client_id VARCHAR(64) NOT NULL,
    flotte_id UUID        NOT NULL,
    drone_id  VARCHAR(64) NOT NULL,

    PRIMARY KEY (flotte_id, drone_id),
    FOREIGN KEY (client_id, flotte_id) REFERENCES flotte (client_id, id),
    FOREIGN KEY (client_id, drone_id) REFERENCES drone (client_id, drone_id)
);