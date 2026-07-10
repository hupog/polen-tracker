package com.hugogonzalez.polentracker.domain;
import java.math.BigDecimal;
import java.time.Instant;
public record PollenMeasurement(GeographicLocation location, PollenType pollenType, MetricType metricType, BigDecimal value, MeasurementUnit unit, DataNature dataNature, Instant validAt) {}
