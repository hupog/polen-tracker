package com.hugogonzalez.polentracker.core.adapter.out.persistence.measurement;

import com.hugogonzalez.polentracker.core.application.model.StoredPollenMeasurement;
import com.hugogonzalez.polentracker.core.application.port.out.PollenMeasurementStore;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class JpaPollenMeasurementStore implements PollenMeasurementStore {
  private final SpringDataPollenMeasurementRepository repository;

  public JpaPollenMeasurementStore(SpringDataPollenMeasurementRepository repository) {
    this.repository = repository;
  }

  @Override
  @Transactional
  public void saveIfAbsent(StoredPollenMeasurement measurement) {
    if (repository.existsByEventId(measurement.eventId())
        || repository.existsByCollectionIdAndLogicalKey(
            measurement.collectionId(), measurement.logicalKey())) {
      return;
    }
    repository.save(new PollenMeasurementJpaEntity(measurement));
  }

  @Override
  @Transactional(readOnly = true)
  public List<StoredPollenMeasurement> findByCollection(UUID collectionId) {
    return repository.findAllByCollectionIdOrderByValidAtAscPollenTypeAsc(collectionId).stream()
        .map(PollenMeasurementJpaEntity::toDomain)
        .toList();
  }
}
