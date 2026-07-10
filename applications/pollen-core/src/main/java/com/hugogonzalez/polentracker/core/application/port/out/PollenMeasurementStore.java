package com.hugogonzalez.polentracker.core.application.port.out;

import com.hugogonzalez.polentracker.core.application.model.StoredPollenMeasurement;
import java.util.List;
import java.util.UUID;

public interface PollenMeasurementStore {
  void saveIfAbsent(StoredPollenMeasurement measurement);

  List<StoredPollenMeasurement> findByCollection(UUID collectionId);
}
