package com.hugogonzalez.polentracker.core.adapter.in.web;

import com.hugogonzalez.polentracker.core.application.model.StoredPollenMeasurement;
import com.hugogonzalez.polentracker.core.application.port.in.QueryPollenMeasurementsUseCase;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/collections/{collectionId}/measurements")
public class PollenMeasurementController {
  private final QueryPollenMeasurementsUseCase query;

  public PollenMeasurementController(QueryPollenMeasurementsUseCase query) {
    this.query = query;
  }

  @GetMapping
  List<StoredPollenMeasurement> findByCollection(@PathVariable UUID collectionId) {
    return query.findByCollection(collectionId);
  }
}
