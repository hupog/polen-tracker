package com.hugogonzalez.polentracker.core.application.service;

import com.hugogonzalez.polentracker.core.application.model.StartCollectionCommand;
import com.hugogonzalez.polentracker.core.application.port.in.*;
import com.hugogonzalez.polentracker.core.application.port.out.*;
import com.hugogonzalez.polentracker.core.domain.model.PollenCollection;
import com.hugogonzalez.polentracker.messaging.*;
import java.time.Instant;
import java.util.*;

public class CollectionApplicationService
    implements StartCollectionUseCase, QueryCollectionsUseCase, HandleCollectionResultUseCase {
  private final CollectionStore store;
  private final CollectionRequestPublisher publisher;

  public CollectionApplicationService(CollectionStore store, CollectionRequestPublisher publisher) {
    this.store = store;
    this.publisher = publisher;
  }

  @Override
  public PollenCollection start(StartCollectionCommand command) {
    var now = Instant.now();
    var collection =
        store.save(PollenCollection.pending(UUID.randomUUID(), command.sourceType(), now));
    var parameters =
        new CollectionParameters(command.dateFrom(), command.dateTo(), command.location());
    publisher.publish(
        new CollectionRequest(
            "1.0",
            collection.id(),
            now,
            CollectionReason.MANUAL,
            command.sourceType(),
            parameters));
    return collection;
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
    store.find(id).map(c -> c.complete(items, at)).ifPresent(store::save);
  }

  @Override
  public void fail(UUID id, Instant at) {
    store.find(id).map(c -> c.fail(at)).ifPresent(store::save);
  }
}
