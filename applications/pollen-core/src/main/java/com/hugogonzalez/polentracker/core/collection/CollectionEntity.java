package com.hugogonzalez.polentracker.core.collection;

import com.hugogonzalez.polentracker.domain.PollenSourceType;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "collections")
public class CollectionEntity {
    @Id
    private UUID id;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PollenSourceType sourceType;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CollectionStatus status;
    @Column(nullable = false)
    private Instant requestedAt;
    private Instant completedAt;
    private int producedItems;

    protected CollectionEntity() {
    }

    public CollectionEntity(UUID id, PollenSourceType sourceType) {
        this.id = id;
        this.sourceType = sourceType;
        this.status = CollectionStatus.PENDING;
        this.requestedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public PollenSourceType getSourceType() {
        return sourceType;
    }

    public CollectionStatus getStatus() {
        return status;
    }

    public Instant getRequestedAt() {
        return requestedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public int getProducedItems() {
        return producedItems;
    }

    public void complete(int items, Instant at) {
        status = CollectionStatus.COMPLETED;
        producedItems = items;
        completedAt = at;
    }

    public void fail(Instant at) {
        status = CollectionStatus.FAILED;
        completedAt = at;
    }
}
