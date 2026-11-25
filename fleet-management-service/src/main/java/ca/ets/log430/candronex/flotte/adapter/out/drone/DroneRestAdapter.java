package ca.ets.log430.candronex.flotte.adapter.out.drone;

import ca.ets.log430.candronex.commun.domaine.DonneeInvalideException;
import ca.ets.log430.candronex.commun.domaine.RessourceIntrouvableException;
import ca.ets.log430.candronex.flotte.domain.VerificationDronePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;

@Component
class DroneRestAdapter implements VerificationDronePort {
    private final RestClient client;

    DroneRestAdapter(RestClient.Builder builder, @Value("${DRONE_SERVICE_URL:http://drone-service:8081}") String url) {
        var delais = new SimpleClientHttpRequestFactory();
        delais.setConnectTimeout(Duration.ofSeconds(2));
        delais.setReadTimeout(Duration.ofSeconds(3));
        client = builder.baseUrl(url).requestFactory(delais).build();
    }

    @Override
    public void verifier(String droneId) {
        var auth = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        try {
            client.get()
                    .uri("/api/drone-service/v1/drone/{droneId}", droneId)
                    .headers(headers -> headers.setBearerAuth(auth.getToken().getTokenValue()))
                    .retrieve()
                    .onStatus(status -> status.value() == 404, (request, response) -> {
                        throw new DroneIntrouvableException();
                    })
                    .onStatus(status -> status.value() == 400, (request, response) -> {
                        throw new DonneeInvalideException("DRONE_ID_INVALIDE", "Identifiant de drone invalide.");
                    })
                    .toBodilessEntity();
        } catch (RestClientException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Le service des drones est momentanément indisponible.", e);
        }
    }

    private static class DroneIntrouvableException extends RessourceIntrouvableException {
        DroneIntrouvableException() {
            super("DRONE_INTROUVABLE", "Drone introuvable.");
        }
    }
}
