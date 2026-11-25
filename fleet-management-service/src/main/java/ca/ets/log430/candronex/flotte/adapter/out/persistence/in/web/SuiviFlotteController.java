package ca.ets.log430.candronex.flotte.adapter.in.web;

import ca.ets.log430.candronex.commun.domaine.ClientId;
import ca.ets.log430.candronex.commun.domaine.DonneeInvalideException;
import ca.ets.log430.candronex.commun.domaine.RessourceIntrouvableException;
import ca.ets.log430.candronex.flotte.adapter.out.persistence.SuiviFlotteJdbcRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class SuiviFlotteController {
    private final SuiviFlotteJdbcRepository repository;

    public SuiviFlotteController(SuiviFlotteJdbcRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/api/drone-service/v1/flotte/{flotteId}/ordres/{ordreId}/suivi")
    public SuiviFlotteResponse consulter(
            @AuthenticationPrincipal Jwt jeton,
            @PathVariable("flotteId") UUID flotteId,
            @PathVariable("ordreId") UUID ordreId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "taille", defaultValue = "20") int taille,
            @RequestParam(name = "pret", required = false) Boolean pret) {
        if (page < 0 || taille < 1 || taille > 100) {
            throw new DonneeInvalideException("PAGINATION_INVALIDE",
                    "La page doit être positive ou nulle et la taille comprise entre 1 et 100.");
        }

        ClientId clientId = new ClientId(jeton.getClaimAsString("azp"));
        var lignes = repository.lire(clientId, flotteId, ordreId);

        if (lignes.isEmpty()) {
            throw new SuiviIntrouvableException();
        }

        return SuiviFlotteMapper.depuis(flotteId, ordreId, lignes, page, taille, pret);
    }

    private static class SuiviIntrouvableException extends RessourceIntrouvableException {
        SuiviIntrouvableException() {
            super("SUIVI_INTROUVABLE", "Suivi de l’ordre de flotte introuvable.");
        }
    }
}
