package com.hugogonzalez.polentracker.collector.messaging;
import jakarta.persistence.*; import org.hibernate.annotations.JdbcTypeCode; import org.hibernate.type.SqlTypes; import java.time.Instant; import java.util.UUID;
@Entity @Table(name="rabbit_message_trace")
public class RabbitMessageTraceEntity {
 @Id private UUID id; @Column(name="collection_id",nullable=false) private UUID collectionId; @Column(name="message_id") private UUID messageId;
 @Column(nullable=false) private String direction; @Column(name="event_type",nullable=false) private String eventType; @Column(nullable=false) private String status;
 @Column(name="exchange_name") private String exchangeName; @Column(name="routing_key") private String routingKey; @Column(name="queue_name") private String queueName;
 @Column(name="delivery_tag") private Long deliveryTag; @Column(name="consumer_tag") private String consumerTag; @Column(name="correlation_id") private String correlationId;
 @Column(nullable=false) private boolean redelivered; @JdbcTypeCode(SqlTypes.JSON) @Column(nullable=false,columnDefinition="jsonb") private String headers; @Column(nullable=false,columnDefinition="text") private String payload;
 @Column(name="occurred_at",nullable=false) private Instant occurredAt;
 protected RabbitMessageTraceEntity() {}
 public RabbitMessageTraceEntity(UUID c, UUID m, String d, String e, String s, String x, String r, String q, Long t, String ct, String co, boolean rd, String h, String p) {
  id=UUID.randomUUID(); collectionId=c; messageId=m; direction=d; eventType=e; status=s; exchangeName=x; routingKey=r; queueName=q; deliveryTag=t; consumerTag=ct; correlationId=co; redelivered=rd; headers=h; payload=p; occurredAt=Instant.now();
 }
}
