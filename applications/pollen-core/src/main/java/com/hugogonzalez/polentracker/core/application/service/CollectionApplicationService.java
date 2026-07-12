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
  private final CollectionSubmissionStore submissions;

  public CollectionApplicationService(CollectionStore store, CollectionSubmissionStore submissions) {
    this.store = store;
    this.submissions = submissions;
  }

  @Override
  public PollenCollection start(StartCollectionCommand command) {
    var now = Instant.now();
    var collection = PollenCollection.pending(UUID.randomUUID(), command.sourceType(), now);
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
    return submissions.submit(collection, request);
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
