package com.hugogonzalez.polentracker.core.adapter.out.persistence.outbox;

import java.time.*;
import java.util.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.*;

@Component
public class CollectionRequestOutboxDispatcher {
  private static final Logger log = LoggerFactory.getLogger(CollectionRequestOutboxDispatcher.class);
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
    var claimed = outbox.lockAvailableBatch(batchSize).stream()
        .map(entry -> entry.claim(workerId, leaseUntil))
        .toList();
    if (!claimed.isEmpty()) {
      log.info("Outbox batch claimed count={} worker_id={} lease_until={}", claimed.size(), workerId, leaseUntil);
    }
    return claimed;
  }

  @Transactional
  public boolean renew(ClaimedOutboxEntry claimed, Duration lease) {
    var renewed = outbox
        .lockById(claimed.id())
        .map(
            entry ->
                entry.renew(
                    claimed.workerId(), claimed.claimToken(), clock.instant().plus(lease)))
        .orElse(false);
    if (renewed) log.debug("Outbox lease renewed outbox_id={}", claimed.id());
    return renewed;
  }

  @Transactional
  public boolean published(ClaimedOutboxEntry claimed) {
    return outbox
        .lockById(claimed.id())
        .map(
            entry ->
                entry.published(
                    claimed.workerId(), claimed.claimToken(), clock.instant()))
        .orElse(false);
  }

  @Transactional
  public void failed(ClaimedOutboxEntry claimed, Throwable error) {
    outbox
        .lockById(claimed.id())
        .ifPresent(
            entry -> {
              var delaySeconds = Math.min(60L, 1L << Math.min(entry.attempts(), 6));
              entry.failed(
                  claimed.workerId(),
                  claimed.claimToken(),
                  error.getMessage(),
                  clock.instant().plusSeconds(delaySeconds));
            });
  }
}
