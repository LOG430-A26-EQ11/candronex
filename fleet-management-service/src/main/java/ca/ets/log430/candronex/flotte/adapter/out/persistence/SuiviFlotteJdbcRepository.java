package ca.ets.log430.candronex.flotte.adapter.out.persistence;

import ca.ets.log430.candronex.commun.domaine.ClientId;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public class SuiviFlotteJdbcRepository {
    private final JdbcTemplate jdbc;

    public SuiviFlotteJdbcRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<LigneSuivi> lire(ClientId clientId, UUID flotteId, UUID ordreId) {
        return jdbc.query("""
                SELECT o.etat_ordre, o.version_projection, o.mis_a_jour_le,
                       s.drone_id, s.type_service, s.etat_configuration,
                       s.etat_connectivite, s.erreur
                FROM suivi_ordre_flotte o
                LEFT JOIN suivi_service_drone s ON s.ordre_id = o.ordre_id
                WHERE o.client_id = ? AND o.flotte_id = ? AND o.ordre_id = ?
                ORDER BY s.drone_id, s.type_service
                """, (ligne, numero) -> new LigneSuivi(
                ligne.getString("etat_ordre"),
                ligne.getLong("version_projection"),
                ligne.getObject("mis_a_jour_le", OffsetDateTime.class).toInstant(),
                ligne.getString("drone_id"),
                ligne.getString("type_service"),
                ligne.getString("etat_configuration"),
                ligne.getString("etat_connectivite"),
                ligne.getString("erreur")
        ), clientId.valeur(), flotteId, ordreId);
    }

    public record LigneSuivi(
            String etatOrdre,
            long versionProjection,
            Instant misAJourLe,
            String droneId,
            String typeService,
            String etatConfiguration,
            String etatConnectivite,
            String erreur) {}
}