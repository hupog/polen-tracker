package com.hugogonzalez.polentracker.core.adapter.out.persistence.outbox;

import java.time.*;
import java.util.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CollectionRequestOutboxDispatcher {
  private final SpringDataCollectionRequestOutboxRepository outbox;
  private final Clock clock;

  public CollectionRequestOutboxDispatcher(
      SpringDataCollectionRequestOutboxRepository outbox, Clock clock) {
    this.outbox = outbox;
    this.clock = clock;
  }

  @Transactional
  public List<ClaimedOutboxEntry> claim(int batchSize, UUID workerId, Duration lease) {
    var leaseUntil = clock.instant().plus(lease);
    return outbox.lockAvailableBatch(batchSize).stream()
        .map(entry -> entry.claim(workerId, leaseUntil))
        .toList();
  }

  @Transactional
  public void published(ClaimedOutboxEntry claimed) {
    outbox
        .findById(claimed.id())
        .ifPresent(entry -> entry.published(claimed.workerId(), clock.instant()));
  }

  @Transactional
  public void failed(ClaimedOutboxEntry claimed, Throwable error) {
    outbox
        .findById(claimed.id())
        .ifPresent(
            entry -> {
              var delaySeconds = Math.min(60L, 1L << Math.min(entry.attempts(), 6));
              entry.failed(
                  claimed.workerId(),
                  error.getMessage(),
                  clock.instant().plusSeconds(delaySeconds));
            });
  }
}
