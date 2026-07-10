package com.hugogonzalez.polentracker.core.messaging;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface RabbitMessageTraceRepository extends JpaRepository<RabbitMessageTraceEntity, UUID> {}
