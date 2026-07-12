package com.hugogonzalez.polentracker.core.adapter.out.persistence.outbox;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "collection_request_outbox")
public class CollectionRequestOutboxJpaEntity {
  @Id private UUID id;

  @Column(name = "request_id", nullable = false, unique = true)
  private UUID requestId;

  @Column(name = "event_type", nullable = false)
  private String eventType;

  @Column(nullable = false, columnDefinition = "text")
  private String payload;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Status status;

  @Column(nullable = false)
  private int attempts;

  @Column(name = "available_at", nullable = false)
  private Instant availableAt;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "published_at")
  private Instant publishedAt;

  @Column(name = "last_error", columnDefinition = "text")
  private String lastError;

  @Column(name = "locked_by")
  private UUID lockedBy;

  @Column(name = "locked_until")
  private Instant lockedUntil;

  protected CollectionRequestOutboxJpaEntity() {}

  public CollectionRequestOutboxJpaEntity(
      UUID id, UUID requestId, String eventType, String payload, Instant createdAt) {
    this.id = id;
    this.requestId = requestId;
    this.eventType = eventType;
    this.payload = payload;
    this.status = Status.PENDING;
    this.availableAt = createdAt;
    this.createdAt = createdAt;
  }

  public ClaimedOutboxEntry claim(UUID workerId, Instant leaseUntil) {
    status = Status.PROCESSING;
    lockedBy = workerId;
    lockedUntil = leaseUntil;
    return new ClaimedOutboxEntry(id, payload, workerId);
  }

  public boolean published(UUID workerId, Instant at) {
    if (status != Status.PROCESSING || !workerId.equals(lockedBy)) return false;
    status = Status.PUBLISHED;
    publishedAt = at;
    lastError = null;
    lockedBy = null;
    lockedUntil = null;
    return true;
  }

  public boolean failed(UUID workerId, String error, Instant retryAt) {
    if (status != Status.PROCESSING || !workerId.equals(lockedBy)) return false;
    status = Status.PENDING;
    attempts++;
    availableAt = retryAt;
    lastError =
        error == null
            ? "Unknown publication error"
            : error.substring(0, Math.min(4000, error.length()));
    lockedBy = null;
    lockedUntil = null;
    return true;
  }

  public int attempts() {
    return attempts;
  }

  public enum Status {
    PENDING,
    PROCESSING,
    PUBLISHED
  }
}
