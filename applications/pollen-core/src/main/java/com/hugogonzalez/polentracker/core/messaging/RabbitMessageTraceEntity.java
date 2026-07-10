package com.hugogonzalez.polentracker.core.messaging;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "rabbit_message_trace")
public class RabbitMessageTraceEntity {
    @Id private UUID id;
    @Column(name = "collection_id", nullable = false) private UUID collectionId;
    @Column(name = "message_id") private UUID messageId;
    @Column(nullable = false) private String direction;
    @Column(name = "event_type", nullable = false) private String eventType;
    @Column(nullable = false) private String status;
    @Column(name = "exchange_name") private String exchangeName;
    @Column(name = "routing_key") private String routingKey;
    @Column(name = "queue_name") private String queueName;
    @Column(name = "delivery_tag") private Long deliveryTag;
    @Column(name = "consumer_tag") private String consumerTag;
    @Column(name = "correlation_id") private String correlationId;
    @Column(nullable = false) private boolean redelivered;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb") private String headers;
    @Column(nullable = false, columnDefinition = "text") private String payload;
    @Column(name = "occurred_at", nullable = false) private Instant occurredAt;
    @Column(name = "processed_at") private Instant processedAt;
    @Column(name = "error_message") private String errorMessage;

    protected RabbitMessageTraceEntity() {}

    public RabbitMessageTraceEntity(UUID collectionId, UUID messageId, String direction, String eventType,
                                    String status, String exchangeName, String routingKey, String queueName,
                                    Long deliveryTag, String consumerTag, String correlationId, boolean redelivered,
                                    String headers, String payload) {
        this.id = UUID.randomUUID(); this.collectionId = collectionId; this.messageId = messageId;
        this.direction = direction; this.eventType = eventType; this.status = status;
        this.exchangeName = exchangeName; this.routingKey = routingKey; this.queueName = queueName;
        this.deliveryTag = deliveryTag; this.consumerTag = consumerTag; this.correlationId = correlationId;
        this.redelivered = redelivered; this.headers = headers; this.payload = payload;
        this.occurredAt = Instant.now();
    }
}
