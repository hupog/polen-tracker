package com.hugogonzalez.polentracker.messaging;
import java.time.Instant; import java.util.UUID;
public record CollectionCompleted(String schemaVersion, UUID eventId, UUID requestId, int producedItems, Instant completedAt) {}
