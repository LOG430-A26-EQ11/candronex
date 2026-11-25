package ca.ets.log430.candronex.commande.application;

import ca.ets.log430.candronex.commande.domain.ConsultationFlottePort;
import ca.ets.log430.candronex.commun.domaine.DonneeInvalideException;
import ca.ets.log430.candronex.commun.domaine.RessourceIntrouvableException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class OrdreFlotteService {
    private final ConsultationFlottePort consultationFlotte;
    private final CommandeService commandeService;

    public OrdreFlotteService(ConsultationFlottePort consultationFlotte,
                              CommandeService commandeService) {
        this.consultationFlotte = consultationFlotte;
        this.commandeService = commandeService;
    }

    public ResultatCommande passer(String clientId, String cleIdempotence,
                                    UUID flotteId, List<String> typesService) {
        List<String> drones = consultationFlotte.trouverDrones(flotteId)
                .orElseThrow(FlotteIntrouvableException::new);

        if (drones.isEmpty()) {
            throw new DonneeInvalideException(
                    "FLOTTE_VIDE", "La flotte doit contenir au moins un drone.");
        }

        List<PasserCommandeCommand.Element> elements = drones.stream()
                .flatMap(droneId -> typesService.stream()
                        .map(type -> new PasserCommandeCommand.Element(droneId, type)))
                .toList();

        return commandeService.passer(
                new PasserCommandeCommand(clientId, cleIdempotence, elements));
    }

    private static class FlotteIntrouvableException extends RessourceIntrouvableException {
        FlotteIntrouvableException() {
            super("FLOTTE_INTROUVABLE", "Flotte introuvable.");
        }
    }
}