package com.hugogonzalez.polentracker.core.application.port.in;

import java.time.Instant;
import java.util.UUID;

public interface HandleCollectionResultUseCase {
  void complete(UUID id, int items, Instant at);

  void fail(UUID id, Instant at);
}
