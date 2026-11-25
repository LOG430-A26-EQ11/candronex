package ca.ets.log430.candronex.flotte.domain;

import ca.ets.log430.candronex.commun.domaine.ClientId;
import java.util.UUID;

public record Flotte(UUID id, ClientId clientId, String nom) {


    public static Flotte creer(ClientId clientId, String nom) {
    return new Flotte(UUID.randomUUID(), clientId, nom);
}
}