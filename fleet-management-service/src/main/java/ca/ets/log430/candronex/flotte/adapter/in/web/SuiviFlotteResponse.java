package ca.ets.log430.candronex.flotte.adapter.in.web;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record SuiviFlotteResponse(
        UUID flotteId,
        UUID ordreId,
        String etatOrdre,
        long versionProjection,
        Instant misAJourLe,
        Map<String, Long> compteursConfiguration,
        double progressionConfigurationPourcent,
        long nombreDronesConnectes,
        long nombreDronesPrets,
        int page,
        int taille,
        int totalDrones,
        int totalDronesFiltres,
        List<DroneSuivi> drones) {

    public record DroneSuivi(
            String droneId,
            boolean pret,
            List<ServiceSuivi> services) {}

    public record ServiceSuivi(
            String typeService,
            String etatConfiguration,
            String etatConnectivite,
            String erreur) {}
}
