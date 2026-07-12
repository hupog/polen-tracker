package com.hugogonzalez.polentracker.core.adapter.out.persistence.outbox;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface SpringDataCollectionRequestOutboxRepository
    extends JpaRepository<CollectionRequestOutboxJpaEntity, UUID> {

  @Query(
      value =
          """
          SELECT *
          FROM collection_request_outbox
          WHERE (status = 'PENDING' AND available_at <= now())
             OR (status = 'PROCESSING' AND locked_until <= now())
          ORDER BY created_at
          FOR UPDATE SKIP LOCKED
          LIMIT :batchSize
          """,
      nativeQuery = true)
  List<CollectionRequestOutboxJpaEntity> lockAvailableBatch(@Param("batchSize") int batchSize);
}
