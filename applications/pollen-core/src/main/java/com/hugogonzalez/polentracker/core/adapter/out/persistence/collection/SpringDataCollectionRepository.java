package com.hugogonzalez.polentracker.core.adapter.out.persistence.collection;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface SpringDataCollectionRepository extends JpaRepository<CollectionJpaEntity, UUID> {}
