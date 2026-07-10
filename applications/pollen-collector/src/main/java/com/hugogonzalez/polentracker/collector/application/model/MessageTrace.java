package com.hugogonzalez.polentracker.collector.application.model;

import java.time.Instant;
import java.util.UUID;

public record MessageTrace(
    UUID id,
    UUID collectionId,
    UUID messageId,
    String direction,
    String eventType,
    String status,
    String exchange,
    String routingKey,
    String queue,
    Long deliveryTag,
    String consumerTag,
    String correlationId,
    boolean redelivered,
    String headers,
    String payload,
    Instant occurredAt) {}
