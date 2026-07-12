package com.hugogonzalez.polentracker.core.adapter.out.persistence.collection;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hugogonzalez.polentracker.core.adapter.out.persistence.outbox.*;
import com.hugogonzalez.polentracker.core.application.port.out.CollectionSubmissionStore;
import com.hugogonzalez.polentracker.core.domain.model.PollenCollection;
import com.hugogonzalez.polentracker.messaging.CollectionRequest;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class JpaCollectionSubmissionStore implements CollectionSubmissionStore {
  private final SpringDataCollectionRepository collections;
  private final SpringDataCollectionRequestOutboxRepository outbox;
  private final ObjectMapper mapper;

  public JpaCollectionSubmissionStore(
      SpringDataCollectionRepository collections,
      SpringDataCollectionRequestOutboxRepository outbox,
      ObjectMapper mapper) {
    this.collections = collections;
    this.outbox = outbox;
    this.mapper = mapper;
  }

  @Override
  @Transactional
  public PollenCollection submit(PollenCollection collection, CollectionRequest request) {
    try {
      var saved = collections.save(new CollectionJpaEntity(collection)).toDomain();
      outbox.save(
          new CollectionRequestOutboxJpaEntity(
              UUID.randomUUID(),
              request.requestId(),
              CollectionRequest.class.getName(),
              mapper.writeValueAsString(request),
              collection.requestedAt()));
      return saved;
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("No se pudo serializar la solicitud de recolección", e);
    }
  }
}
