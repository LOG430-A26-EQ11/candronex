package ca.ets.log430.candronex.commande.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConsultationFlottePort {
    Optional<List<String>> trouverDrones(UUID flotteId);
}