package com.hugogonzalez.polentracker.core.domain.model;

import com.hugogonzalez.polentracker.domain.PollenSourceType;
import java.time.Instant;
import java.util.UUID;

public record PollenCollection(UUID id, PollenSourceType sourceType, CollectionStatus status, Instant requestedAt,
                               Instant completedAt, int producedItems) {
    public static PollenCollection pending(UUID id, PollenSourceType sourceType, Instant requestedAt) {
        return new PollenCollection(id, sourceType, CollectionStatus.PENDING, requestedAt, null, 0);
    }
    public PollenCollection complete(int items, Instant at) {
        return new PollenCollection(id, sourceType, CollectionStatus.COMPLETED, requestedAt, at, items);
    }
    public PollenCollection fail(Instant at) {
        return new PollenCollection(id, sourceType, CollectionStatus.FAILED, requestedAt, at, producedItems);
    }
}
