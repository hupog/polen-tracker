package com.hugogonzalez.polentracker.core.adapter.out.persistence.measurement;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface SpringDataPollenMeasurementRepository
    extends JpaRepository<PollenMeasurementJpaEntity, UUID> {
  boolean existsByEventId(UUID eventId);

  boolean existsByCollectionIdAndLogicalKey(UUID collectionId, String logicalKey);

  List<PollenMeasurementJpaEntity> findAllByCollectionIdOrderByValidAtAscPollenTypeAsc(
      UUID collectionId);
}
