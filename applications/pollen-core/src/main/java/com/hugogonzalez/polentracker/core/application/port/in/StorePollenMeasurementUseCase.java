package com.hugogonzalez.polentracker.core.application.port.in;

import com.hugogonzalez.polentracker.messaging.PollenMeasurementCollected;

public interface StorePollenMeasurementUseCase {
  void store(PollenMeasurementCollected event);
}
