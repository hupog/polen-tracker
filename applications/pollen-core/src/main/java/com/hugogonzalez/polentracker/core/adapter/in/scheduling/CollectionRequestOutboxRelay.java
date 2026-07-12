package com.hugogonzalez.polentracker.core.adapter.in.scheduling;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hugogonzalez.polentracker.core.adapter.out.persistence.outbox.*;
import com.hugogonzalez.polentracker.core.application.port.out.CollectionRequestPublisher;
import com.hugogonzalez.polentracker.messaging.CollectionRequest;
import java.time.*;
import java.util.UUID;
import java.util.concurrent.RejectedExecutionException;
import org.springframework.beans.factory.annotation.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component
public class CollectionRequestOutboxRelay {
  private final CollectionRequestOutboxDispatcher dispatcher;
  private final CollectionRequestPublisher publisher;
  private final ObjectMapper mapper;
  private final ThreadPoolTaskExecutor executor;
  private final int batchSize;
  private final Duration lease;
  private final UUID workerId = UUID.randomUUID();

  public CollectionRequestOutboxRelay(
      CollectionRequestOutboxDispatcher dispatcher,
      CollectionRequestPublisher publisher,
      ObjectMapper mapper,
      @Qualifier("outboxExecutor") ThreadPoolTaskExecutor executor,
      @Value("${messaging.outbox.batch-size:50}") int batchSize,
      @Value("${messaging.outbox.lease:30s}") Duration lease) {
    this.dispatcher = dispatcher;
    this.publisher = publisher;
    this.mapper = mapper;
    this.executor = executor;
    this.batchSize = batchSize;
    this.lease = lease;
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
    try {
      publisher.publish(mapper.readValue(claimed.payload(), CollectionRequest.class));
      dispatcher.published(claimed);
    } catch (Exception e) {
      dispatcher.failed(claimed, e);
    }
  }
}
