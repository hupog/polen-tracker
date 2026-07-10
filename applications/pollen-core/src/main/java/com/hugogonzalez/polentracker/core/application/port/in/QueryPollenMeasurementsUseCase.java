package com.hugogonzalez.polentracker.core.application.port.in;

import com.hugogonzalez.polentracker.core.application.model.StoredPollenMeasurement;
import java.util.List;
import java.util.UUID;

public interface QueryPollenMeasurementsUseCase {
  List<StoredPollenMeasurement> findByCollection(UUID collectionId);
}
