package ca.ets.log430.candronex.flotte.adapter.out.persistence;

import ca.ets.log430.candronex.commun.domaine.ClientId;
import ca.ets.log430.candronex.flotte.domain.Flotte;
import ca.ets.log430.candronex.flotte.domain.FlotteRepository;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.Optional;
import java.util.List;
import java.util.UUID;

@Repository
public class JpaFlotteRepository implements FlotteRepository {
    private final FlotteJpaRepository repository;

    private final JdbcTemplate jdbc;

    public JpaFlotteRepository(FlotteJpaRepository repository, JdbcTemplate jdbc) {
        this.repository = repository;
        this.jdbc = jdbc;
    }

    @Override
    public void sauvegarder(Flotte flotte) {
        FlotteJpaEntity entite = new FlotteJpaEntity(
                flotte.id(),
                flotte.clientId().valeur(),
                flotte.nom()
        );
        repository.save(entite);
    }
    @Override
    public List<String> listerDrones(ClientId clientId, UUID flotteId) {
        return jdbc.queryForList("""
                SELECT drone_id
                FROM flotte_drone
                WHERE client_id = ? AND flotte_id = ?
                ORDER BY drone_id
                """, String.class, clientId.valeur(), flotteId);
    }

    @Override
    public Optional<Flotte> trouver(ClientId clientId, UUID id) {
        return repository.findByIdAndClientId(id, clientId.valeur())
                .map(entite -> new Flotte(id, clientId, entite.getNom()));
    }
    @Override
    public void ajouterDrone(ClientId clientId, UUID flotteId, String droneId) {
        jdbc.update("""
                INSERT INTO flotte_drone (client_id, flotte_id, drone_id)
                VALUES (?, ?, ?)
                ON CONFLICT DO NOTHING
                """, clientId.valeur(), flotteId, droneId);
    }
}
