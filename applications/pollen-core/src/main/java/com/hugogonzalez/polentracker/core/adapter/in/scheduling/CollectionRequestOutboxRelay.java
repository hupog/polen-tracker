package com.hugogonzalez.polentracker.core.adapter.in.scheduling;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hugogonzalez.polentracker.core.adapter.out.persistence.outbox.*;
import com.hugogonzalez.polentracker.core.application.port.out.CollectionRequestPublisher;
import com.hugogonzalez.polentracker.messaging.CollectionRequest;
import java.time.*;
import java.util.UUID;
import java.util.concurrent.*;
import org.springframework.beans.factory.annotation.*;
import org.slf4j.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.*;
import org.springframework.stereotype.Component;

@Component
public class CollectionRequestOutboxRelay {
  private static final Logger log = LoggerFactory.getLogger(CollectionRequestOutboxRelay.class);
  private final CollectionRequestOutboxDispatcher dispatcher;
  private final CollectionRequestPublisher publisher;
  private final ObjectMapper mapper;
  private final ThreadPoolTaskExecutor executor;
  private final ThreadPoolTaskScheduler leaseScheduler;
  private final int batchSize;
  private final Duration lease;
  private final UUID workerId = UUID.randomUUID();

  public CollectionRequestOutboxRelay(
      CollectionRequestOutboxDispatcher dispatcher,
      CollectionRequestPublisher publisher,
      ObjectMapper mapper,
      @Qualifier("outboxExecutor") ThreadPoolTaskExecutor executor,
      @Qualifier("outboxLeaseScheduler") ThreadPoolTaskScheduler leaseScheduler,
      @Value("${messaging.outbox.batch-size:50}") int batchSize,
      @Value("${messaging.outbox.lease:30s}") Duration lease) {
    this.dispatcher = dispatcher;
    this.publisher = publisher;
    this.mapper = mapper;
    this.executor = executor;
    this.leaseScheduler = leaseScheduler;
    this.batchSize = batchSize;
    this.lease = lease;
    if (lease.dividedBy(3).isZero()) {
      throw new IllegalArgumentException("The outbox lease must be greater than zero");
    }
  }

  @Scheduled(fixedDelayString = "${messaging.outbox.fixed-delay:1s}")
  public void publishPending() {
    var pool = executor.getThreadPoolExecutor();
    var capacity =
        pool.getMaximumPoolSize() - pool.getActiveCount() + pool.getQueue().remainingCapacity();
    if (capacity <= 0) return;

    for (var claimed : dispatcher.claim(Math.min(batchSize, capacity), workerId, lease)) {
      try {
        executor.execute(() -> publish(claimed));
      } catch (RejectedExecutionException e) {
        dispatcher.failed(claimed, e);
      }
    }
  }

  private void publish(ClaimedOutboxEntry claimed) {
    try (var request = MDC.putCloseable("requestId", claimed.requestId().toString());
        var collection = MDC.putCloseable("collectionId", claimed.requestId().toString())) {
      log.info("Outbox publication started outbox_id={} claim_token={}", claimed.id(), claimed.claimToken());
      var heartbeat =
          leaseScheduler.scheduleAtFixedRate(
              () -> renewLease(claimed), lease.dividedBy(3));
      try {
        publisher.publish(mapper.readValue(claimed.payload(), CollectionRequest.class));
        heartbeat.cancel(false);
        if (!dispatcher.published(claimed)) {
          log.warn("Outbox publication could not be finalized because the claim is stale outbox_id={}", claimed.id());
        } else {
          log.info("Outbox publication completed outbox_id={}", claimed.id());
        }
      } catch (Exception e) {
        heartbeat.cancel(false);
        log.error("Outbox publication failed outbox_id={}", claimed.id(), e);
        dispatcher.failed(claimed, e);
      }
    }
  }

  private void renewLease(ClaimedOutboxEntry claimed) {
    try (var request = MDC.putCloseable("requestId", claimed.requestId().toString());
        var collection = MDC.putCloseable("collectionId", claimed.requestId().toString())) {
      try {
        if (!dispatcher.renew(claimed, lease)) {
          log.warn("Outbox lease was not renewed because the claim is stale outbox_id={}", claimed.id());
        }
      } catch (RuntimeException e) {
        log.error("Outbox lease renewal failed outbox_id={}", claimed.id(), e);
      }
    }
  }
}
