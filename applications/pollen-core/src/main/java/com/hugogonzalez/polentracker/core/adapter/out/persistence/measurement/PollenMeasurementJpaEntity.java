package com.hugogonzalez.polentracker.core.adapter.out.persistence.measurement;

import com.hugogonzalez.polentracker.core.application.model.StoredPollenMeasurement;
import com.hugogonzalez.polentracker.domain.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "pollen_measurements")
class PollenMeasurementJpaEntity {
  @Id UUID id;

  @Column(name = "collection_id", nullable = false)
  UUID collectionId;

  @Column(name = "event_id", nullable = false)
  UUID eventId;

  @Column(name = "logical_key", nullable = false)
  String logicalKey;

  @Enumerated(EnumType.STRING)
  @Column(name = "source_type", nullable = false)
  PollenSourceType sourceType;

  @Column(name = "location_name")
  String locationName;

  @Column(nullable = false)
  double latitude;

  @Column(nullable = false)
  double longitude;

  @Enumerated(EnumType.STRING)
  @Column(name = "pollen_type", nullable = false)
  PollenType pollenType;

  @Enumerated(EnumType.STRING)
  @Column(name = "metric_type", nullable = false)
  MetricType metricType;

  @Column(nullable = false, precision = 18, scale = 6)
  BigDecimal value;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  MeasurementUnit unit;

  @Enumerated(EnumType.STRING)
  @Column(name = "data_nature", nullable = false)
  DataNature dataNature;

  @Column(name = "valid_at", nullable = false)
  Instant validAt;

  @Column(name = "collected_at", nullable = false)
  Instant collectedAt;

  @Column(name = "created_at", nullable = false)
  Instant createdAt;

  protected PollenMeasurementJpaEntity() {}

  PollenMeasurementJpaEntity(StoredPollenMeasurement measurement) {
    id = measurement.id();
    collectionId = measurement.collectionId();
    eventId = measurement.eventId();
    logicalKey = measurement.logicalKey();
    sourceType = measurement.sourceType();
    locationName = measurement.location().name();
    latitude = measurement.location().latitude();
    longitude = measurement.location().longitude();
    pollenType = measurement.pollenType();
    metricType = measurement.metricType();
    value = measurement.value();
    unit = measurement.unit();
    dataNature = measurement.dataNature();
    validAt = measurement.validAt();
    collectedAt = measurement.collectedAt();
    createdAt = measurement.createdAt();
  }

  StoredPollenMeasurement toDomain() {
    return new StoredPollenMeasurement(
        id,
        collectionId,
        eventId,
        logicalKey,
        sourceType,
        new GeographicLocation(locationName, latitude, longitude),
        pollenType,
        metricType,
        value,
        unit,
        dataNature,
        validAt,
        collectedAt,
        createdAt);
  }
}
