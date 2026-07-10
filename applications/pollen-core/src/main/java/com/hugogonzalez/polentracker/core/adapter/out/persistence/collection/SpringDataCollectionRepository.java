package com.hugogonzalez.polentracker.core.adapter.out.persistence.collection;
import org.springframework.data.jpa.repository.JpaRepository; import java.util.UUID;
interface SpringDataCollectionRepository extends JpaRepository<CollectionJpaEntity, UUID> {}
