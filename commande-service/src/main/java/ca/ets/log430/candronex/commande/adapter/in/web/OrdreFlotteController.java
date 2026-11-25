package ca.ets.log430.candronex.commande.adapter.in.web;

import ca.ets.log430.candronex.commande.application.OrdreFlotteService;
import ca.ets.log430.candronex.commande.application.ResultatCommande;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
public class OrdreFlotteController {
    private final OrdreFlotteService ordreFlotteService;

    public OrdreFlotteController(OrdreFlotteService ordreFlotteService) {
        this.ordreFlotteService = ordreFlotteService;
    }

    @PostMapping(CommandeController.CHEMIN + "/flotte")
    public ResponseEntity<CommandeResponse> passer(
            @AuthenticationPrincipal Jwt jeton,
            @RequestHeader(value = "Idempotency-Key", required = false) String cleIdempotence,
            @Valid @RequestBody OrdreFlotteRequest requete) {
        ResultatCommande resultat = ordreFlotteService.passer(
                jeton.getClaimAsString("azp"),
                cleIdempotence,
                requete.flotteId(),
                requete.typesService());

        CommandeResponse corps = CommandeResponse.depuis(resultat.commande());
        if (!resultat.nouvelle()) {
            return ResponseEntity.ok(corps);
        }

        URI emplacement = URI.create(CommandeController.CHEMIN + "/" + corps.commandeId());
        return ResponseEntity.created(emplacement).body(corps);
    }

    public record OrdreFlotteRequest(
            @NotNull UUID flotteId,
            @NotEmpty List<@NotBlank String> typesService) {}
}