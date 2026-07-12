package com.hugogonzalez.polentracker.core.application.service;

import com.hugogonzalez.polentracker.core.application.model.StartCollectionCommand;
import com.hugogonzalez.polentracker.core.application.port.in.*;
import com.hugogonzalez.polentracker.core.application.port.out.*;
import com.hugogonzalez.polentracker.core.domain.model.PollenCollection;
import com.hugogonzalez.polentracker.messaging.*;
import java.time.Instant;
import java.util.*;
import org.slf4j.*;

public class CollectionApplicationService
    implements StartCollectionUseCase, QueryCollectionsUseCase, HandleCollectionResultUseCase {
  private static final Logger log = LoggerFactory.getLogger(CollectionApplicationService.class);
  private final CollectionStore store;
  private final CollectionSubmissionStore submissions;

  public CollectionApplicationService(CollectionStore store, CollectionSubmissionStore submissions) {
    this.store = store;
    this.submissions = submissions;
  }

  @Override
  public PollenCollection start(StartCollectionCommand command) {
    var now = Instant.now();
    var collection = PollenCollection.pending(UUID.randomUUID(), command.sourceType(), now);
    try (var ignored = MDC.putCloseable("collectionId", collection.id().toString())) {
      log.info(
          "Collection submission started source={} date_from={} date_to={} latitude={} longitude={}",
          command.sourceType(),
          command.dateFrom(),
          command.dateTo(),
          command.location().latitude(),
          command.location().longitude());
    var parameters =
        new CollectionParameters(command.dateFrom(), command.dateTo(), command.location());
    var request =
        new CollectionRequest(
            "1.0",
            collection.id(),
            now,
            CollectionReason.MANUAL,
            command.sourceType(),
            parameters);
      var submitted = submissions.submit(collection, request);
      log.info("Collection and outbox request committed status={}", submitted.status());
      return submitted;
    }
  }

  @Override
  public List<PollenCollection> findAll() {
    return store.findAll();
  }

  @Override
  public Optional<PollenCollection> find(UUID id) {
    return store.find(id);
  }

  @Override
  public void complete(UUID id, int items, Instant at) {
    try (var ignored = MDC.putCloseable("collectionId", id.toString())) {
      store.find(id).map(c -> c.complete(items, at)).ifPresent(store::save);
      log.info("Collection completion event applied produced_items={}", items);
    }
  }

  @Override
  public void fail(UUID id, Instant at) {
    try (var ignored = MDC.putCloseable("collectionId", id.toString())) {
      store.find(id).map(c -> c.fail(at)).ifPresent(store::save);
      log.warn("Collection failure event applied");
    }
  }
}
