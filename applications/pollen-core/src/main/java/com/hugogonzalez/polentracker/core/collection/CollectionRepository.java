package com.hugogonzalez.polentracker.core.collection;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CollectionRepository extends JpaRepository<CollectionEntity, UUID> {
}
