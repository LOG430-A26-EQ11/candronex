package ca.ets.log430.candronex.flotte.adapter.in.web;

import ca.ets.log430.candronex.commun.domaine.ClientId;
import ca.ets.log430.candronex.flotte.application.FlotteService;
import ca.ets.log430.candronex.flotte.domain.Flotte;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/drone-service/v1/flotte")
public class FlotteController {
    private final FlotteService flotteService;

    public FlotteController(FlotteService flotteService) {
        this.flotteService = flotteService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FlotteResponse creer(
            @AuthenticationPrincipal Jwt jeton,
            @RequestBody CreerFlotteRequest requete) {
        ClientId clientId = new ClientId(jeton.getClaimAsString("azp"));
        Flotte flotte = flotteService.creer(clientId, requete.nom());
        return new FlotteResponse(flotte.id(), flotte.nom(), List.of());
    }

    @GetMapping("/{flotteId}")
    public FlotteResponse consulter(
            @AuthenticationPrincipal Jwt jeton,
            @PathVariable("flotteId") UUID flotteId) {
        ClientId clientId = new ClientId(jeton.getClaimAsString("azp"));
        Flotte flotte = flotteService.consulter(clientId, flotteId);
        return new FlotteResponse(flotte.id(), flotte.nom(), flotteService.listerDrones(flotte));
    }
    @PutMapping("/{flotteId}/drones/{droneId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void ajouterDrone(
            @AuthenticationPrincipal Jwt jeton,
            @PathVariable("flotteId") UUID flotteId,
            @PathVariable("droneId") String droneId) {
        ClientId clientId = new ClientId(jeton.getClaimAsString("azp"));
        flotteService.ajouterDrone(clientId, flotteId, droneId);
    }

    public record CreerFlotteRequest(String nom) {}

    public record FlotteResponse(UUID id, String nom, List<String> drones) {}
}
