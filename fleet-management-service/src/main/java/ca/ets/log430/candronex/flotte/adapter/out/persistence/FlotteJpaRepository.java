package ca.ets.log430.candronex.flotte.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FlotteJpaRepository extends JpaRepository<FlotteJpaEntity, UUID> {

Optional<FlotteJpaEntity> findByIdAndClientId(UUID id, String clientId);

}


