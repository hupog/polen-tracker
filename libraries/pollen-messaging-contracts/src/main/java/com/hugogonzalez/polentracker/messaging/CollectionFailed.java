package com.hugogonzalez.polentracker.messaging;
import java.time.Instant; import java.util.UUID;
public record CollectionFailed(String schemaVersion, UUID eventId, UUID requestId, String errorCode, String message, Instant failedAt) {}
