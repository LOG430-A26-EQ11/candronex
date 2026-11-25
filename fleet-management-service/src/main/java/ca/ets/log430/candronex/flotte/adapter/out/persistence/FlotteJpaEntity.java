package ca.ets.log430.candronex.flotte.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "flotte")
public class FlotteJpaEntity {
    @Id
    private UUID id;

    @Column(name = "client_id", nullable = false, length = 64)
    private String clientId;

    @Column(nullable = false, length = 100)
    private String nom;

    protected FlotteJpaEntity() {
    }

    public FlotteJpaEntity(UUID id, String clientId, String nom) {
        this.id = id;
        this.clientId = clientId;
        this.nom = nom;
    }

    public String getNom() {
        return nom;
    }
}