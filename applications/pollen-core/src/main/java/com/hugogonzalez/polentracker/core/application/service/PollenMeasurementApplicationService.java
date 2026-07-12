package com.hugogonzalez.polentracker.core.application.service;

import com.hugogonzalez.polentracker.core.application.model.StoredPollenMeasurement;
import com.hugogonzalez.polentracker.core.application.port.in.*;
import com.hugogonzalez.polentracker.core.application.port.out.PollenMeasurementStore;
import com.hugogonzalez.polentracker.messaging.PollenMeasurementCollected;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.slf4j.*;

public class PollenMeasurementApplicationService
    implements StorePollenMeasurementUseCase, QueryPollenMeasurementsUseCase {
  private static final Logger log = LoggerFactory.getLogger(PollenMeasurementApplicationService.class);
  private final PollenMeasurementStore store;

  public PollenMeasurementApplicationService(PollenMeasurementStore store) {
    this.store = store;
  }

  @Override
  public void store(PollenMeasurementCollected event) {
    try (var request = MDC.putCloseable("requestId", event.requestId().toString());
        var collection = MDC.putCloseable("collectionId", event.requestId().toString());
        var message = MDC.putCloseable("messageId", event.eventId().toString())) {
    var payload = event.payload();
    store.saveIfAbsent(
        new StoredPollenMeasurement(
            UUID.randomUUID(),
            event.requestId(),
            event.eventId(),
            event.logicalKey(),
            event.sourceType(),
            payload.location(),
            payload.pollenType(),
            payload.metricType(),
            payload.value(),
            payload.unit(),
            payload.dataNature(),
            payload.validAt(),
            event.collectedAt(),
            Instant.now()));
      log.info(
          "Pollen measurement stored source={} pollen_type={} metric={} valid_at={}",
          event.sourceType(),
          payload.pollenType(),
          payload.metricType(),
          payload.validAt());
    }
  }

  @Override
  public List<StoredPollenMeasurement> findByCollection(UUID collectionId) {
    return store.findByCollection(collectionId);
  }
}
