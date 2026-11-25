package ca.ets.log430.candronex.flotte.domain;

import ca.ets.log430.candronex.commun.domaine.ClientId;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface FlotteRepository {
    void sauvegarder(Flotte flotte);

    Optional<Flotte> trouver(ClientId clientId, UUID id);
    void ajouterDrone(ClientId clientId, UUID flotteId, String droneId);
    List<String> listerDrones(ClientId clientId, UUID flotteId);
}