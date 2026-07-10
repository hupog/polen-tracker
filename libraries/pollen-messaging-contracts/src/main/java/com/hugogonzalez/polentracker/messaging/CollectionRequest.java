package com.hugogonzalez.polentracker.messaging;
import com.hugogonzalez.polentracker.domain.PollenSourceType;
import java.time.Instant; import java.util.UUID;
public record CollectionRequest(String schemaVersion, UUID requestId, Instant requestedAt, CollectionReason reason, PollenSourceType sourceType, CollectionParameters params) {}
