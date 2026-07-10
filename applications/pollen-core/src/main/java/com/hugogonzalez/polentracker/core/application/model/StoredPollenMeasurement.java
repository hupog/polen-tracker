package com.hugogonzalez.polentracker.core.application.model;

import com.hugogonzalez.polentracker.domain.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record StoredPollenMeasurement(
    UUID id,
    UUID collectionId,
    UUID eventId,
    String logicalKey,
    PollenSourceType sourceType,
    GeographicLocation location,
    PollenType pollenType,
    MetricType metricType,
    BigDecimal value,
    MeasurementUnit unit,
    DataNature dataNature,
    Instant validAt,
    Instant collectedAt,
    Instant createdAt) {}
