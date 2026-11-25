package ca.ets.log430.candronex.flotte.adapter.in.web;

import ca.ets.log430.candronex.flotte.adapter.out.persistence.SuiviFlotteJdbcRepository.LigneSuivi;
import ca.ets.log430.candronex.flotte.adapter.in.web.SuiviFlotteResponse.DroneSuivi;
import ca.ets.log430.candronex.flotte.adapter.in.web.SuiviFlotteResponse.ServiceSuivi;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Set;

final class SuiviFlotteMapper {
    private static final Set<String> SERVICES_REQUIS = Set.of("C2", "IMAGERIE");

    private SuiviFlotteMapper() {}
    
    static SuiviFlotteResponse depuis(UUID flotteId, UUID ordreId, List<LigneSuivi> lignes,
                                     int page, int taille, Boolean pret) {
        LigneSuivi ordre = lignes.getFirst();
        List<DroneSuivi> drones = regrouperDrones(lignes);

        Map<String, Long> compteurs = new LinkedHashMap<>();
        long totalServices = 0;
        for (DroneSuivi drone : drones) {
            for (ServiceSuivi service : drone.services()) {
                compteurs.merge(service.etatConfiguration(), 1L, Long::sum);
                totalServices++;
            }
        }

        long servicesTermines = compteurs.getOrDefault("CONFIGURED", 0L)
                + compteurs.getOrDefault("FAILED", 0L);
        double progression = totalServices == 0 ? 0.0 : 100.0 * servicesTermines / totalServices;

        long dronesConnectes = drones.stream()
                .filter(drone -> SERVICES_REQUIS.stream().allMatch(type ->
                        drone.services().stream().anyMatch(service ->
                                type.equals(service.typeService())
                                        && "CONNECTED".equals(service.etatConnectivite()))))
                .count();
        long dronesPrets = drones.stream().filter(DroneSuivi::pret).count();

        List<DroneSuivi> dronesFiltres = drones;
        if (pret != null) {
            dronesFiltres = drones.stream().filter(drone -> drone.pret() == pret).toList();
        }
        int totalDronesFiltres = dronesFiltres.size();
        int debut = (int) Math.min((long) page * taille, totalDronesFiltres);
        int fin = debut + Math.min(taille, totalDronesFiltres - debut);
        return new SuiviFlotteResponse(
                flotteId, ordreId, ordre.etatOrdre(), ordre.versionProjection(),
                ordre.misAJourLe(), compteurs, progression,
                dronesConnectes, dronesPrets, page, taille, drones.size(),
                totalDronesFiltres, dronesFiltres.subList(debut, fin));
    }
    static List<DroneSuivi> regrouperDrones(List<LigneSuivi> lignes) {
        Map<String, List<ServiceSuivi>> servicesParDrone = new LinkedHashMap<>();

        for (LigneSuivi ligne : lignes) {
            if (ligne.droneId() == null) {
                continue;
            }

            servicesParDrone.computeIfAbsent(ligne.droneId(), id -> new ArrayList<>())
                    .add(new ServiceSuivi(
                            ligne.typeService(),
                            ligne.etatConfiguration(),
                            ligne.etatConnectivite(),
                            ligne.erreur()));
        }

        return servicesParDrone.entrySet().stream()
                .map(entree -> new DroneSuivi(
                        entree.getKey(),
                        estPret(entree.getValue()),
                        List.copyOf(entree.getValue())))
                .toList();
    }

    private static boolean estPret(List<ServiceSuivi> services) {
        return SERVICES_REQUIS.stream().allMatch(type ->
                services.stream().anyMatch(service ->
                        type.equals(service.typeService())
                                && "CONFIGURED".equals(service.etatConfiguration())
                                && "CONNECTED".equals(service.etatConnectivite())
                                && service.erreur() == null));
    }
}
