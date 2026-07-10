package com.hugogonzalez.polentracker.collector.adapter.out.persistence.trace;

import com.hugogonzalez.polentracker.collector.application.model.MessageTrace;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "rabbit_message_trace")
class MessageTraceJpaEntity {
  @Id UUID id;

  @Column(name = "collection_id", nullable = false)
  UUID collectionId;

  @Column(name = "message_id")
  UUID messageId;

  @Column(nullable = false)
  String direction;

  @Column(name = "event_type", nullable = false)
  String eventType;

  @Column(nullable = false)
  String status;

  @Column(name = "exchange_name")
  String exchange;

  @Column(name = "routing_key")
  String routingKey;

  @Column(name = "queue_name")
  String queue;

  @Column(name = "delivery_tag")
  Long deliveryTag;

  @Column(name = "consumer_tag")
  String consumerTag;

  @Column(name = "correlation_id")
  String correlationId;

  @Column(nullable = false)
  boolean redelivered;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(nullable = false, columnDefinition = "jsonb")
  String headers;

  @Column(nullable = false, columnDefinition = "text")
  String payload;

  @Column(name = "occurred_at", nullable = false)
  Instant occurredAt;

  @Column(name = "processed_at")
  Instant processedAt;

  @Column(name = "error_message")
  String errorMessage;

  protected MessageTraceJpaEntity() {}

  MessageTraceJpaEntity(MessageTrace t) {
    id = t.id();
    collectionId = t.collectionId();
    messageId = t.messageId();
    direction = t.direction();
    eventType = t.eventType();
    status = t.status();
    exchange = t.exchange();
    routingKey = t.routingKey();
    queue = t.queue();
    deliveryTag = t.deliveryTag();
    consumerTag = t.consumerTag();
    correlationId = t.correlationId();
    redelivered = t.redelivered();
    headers = t.headers();
    payload = t.payload();
    occurredAt = t.occurredAt();
  }
}
