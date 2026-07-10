package com.hugogonzalez.polentracker.collector.application.port.out;

import com.hugogonzalez.polentracker.messaging.*;

public interface CollectionEventPublisher {
  void measurement(PollenMeasurementCollected event);

  void completed(CollectionCompleted event);

  void failed(CollectionFailed event);
}
