package com.hugogonzalez.polentracker.messaging;
import com.hugogonzalez.polentracker.domain.*;
import java.time.Instant; import java.util.UUID;
public record PollenMeasurementCollected(String schemaVersion, UUID eventId, UUID requestId, PollenSourceType sourceType, Instant collectedAt, String logicalKey, PollenMeasurement payload) {}
