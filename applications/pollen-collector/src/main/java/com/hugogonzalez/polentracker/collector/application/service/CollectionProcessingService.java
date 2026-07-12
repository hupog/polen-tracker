package com.hugogonzalez.polentracker.collector.application.service;

import com.hugogonzalez.polentracker.collector.application.port.in.ProcessCollectionUseCase;
import com.hugogonzalez.polentracker.collector.application.port.out.CollectionEventPublisher;
import com.hugogonzalez.polentracker.messaging.*;
import java.time.Instant;
import java.util.UUID;
import org.slf4j.*;

public class CollectionProcessingService implements ProcessCollectionUseCase {
  private static final Logger log = LoggerFactory.getLogger(CollectionProcessingService.class);
  private final PollenSourceRegistry sources;
  private final CollectionEventPublisher events;

  public CollectionProcessingService(
      PollenSourceRegistry sources, CollectionEventPublisher events) {
    this.sources = sources;
    this.events = events;
  }

  public void process(CollectionRequest request) {
    var started = System.nanoTime();
    log.info("Collection processing started source={}", request.sourceType());
    var measurements = sources.get(request.sourceType()).collect(request.params());
    log.info("Pollen source collection completed measurement_count={}", measurements.size());
    for (var m : measurements) {
      var id = UUID.randomUUID();
      var key =
          request.sourceType()
              + "|"
              + m.location().latitude()
              + "|"
              + m.location().longitude()
              + "|"
              + m.pollenType()
              + "|"
              + m.validAt()
              + "|"
              + m.metricType();
      events.measurement(
          new PollenMeasurementCollected(
              "1.0", id, request.requestId(), request.sourceType(), Instant.now(), key, m));
    }
    events.completed(
        new CollectionCompleted(
            "1.0", UUID.randomUUID(), request.requestId(), measurements.size(), Instant.now()));
    log.info(
        "Collection processing completed measurement_count={} duration_ms={}",
        measurements.size(),
        (System.nanoTime() - started) / 1_000_000);
  }
}
