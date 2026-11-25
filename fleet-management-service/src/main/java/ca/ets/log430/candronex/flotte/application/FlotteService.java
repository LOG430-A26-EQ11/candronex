package ca.ets.log430.candronex.flotte.application;

import ca.ets.log430.candronex.commun.domaine.ClientId;
import ca.ets.log430.candronex.commun.domaine.DonneeInvalideException;
import ca.ets.log430.candronex.flotte.domain.Flotte;
import ca.ets.log430.candronex.flotte.domain.FlotteRepository;
import org.springframework.stereotype.Service;
import ca.ets.log430.candronex.flotte.domain.FlotteIntrouvableException;
import ca.ets.log430.candronex.flotte.domain.VerificationDronePort;
import java.util.List;
import java.util.UUID;

@Service
public class FlotteService {
    private final FlotteRepository repository;
    private final VerificationDronePort verificationDrone;

    public FlotteService(FlotteRepository repository, VerificationDronePort verificationDrone) {
        this.repository = repository;
        this.verificationDrone = verificationDrone;
    }

    public Flotte creer(ClientId clientId, String nom) {
        if (nom == null || nom.isBlank() || nom.length() > 100) {
            throw new DonneeInvalideException(
                    "NOM_FLOTTE_INVALIDE",
                    "Le nom de la flotte doit contenir entre 1 et 100 caractères."
            );
        }

        Flotte flotte = Flotte.creer(clientId, nom);
        repository.sauvegarder(flotte);
        return flotte;
    }
    public Flotte consulter(ClientId clientId, UUID id) {
    return repository.trouver(clientId, id)
            .orElseThrow(FlotteIntrouvableException::new);
    }
    public void ajouterDrone(ClientId clientId, UUID flotteId, String droneId) {
        consulter(clientId, flotteId);
        verificationDrone.verifier(droneId);
        repository.ajouterDrone(clientId, flotteId, droneId);
    }
    public List<String> listerDrones(Flotte flotte) {
        return repository.listerDrones(flotte.clientId(), flotte.id());
    }
}

