package com.hugogonzalez.polentracker.core.application.port.out;

import com.hugogonzalez.polentracker.core.domain.model.PollenCollection;
import com.hugogonzalez.polentracker.messaging.CollectionRequest;

/** Persists a collection and its integration request as one atomic operation. */
public interface CollectionSubmissionStore {
  PollenCollection submit(PollenCollection collection, CollectionRequest request);
}
