package com.hugogonzalez.polentracker.collector.adapter.out.persistence.trace;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface SpringDataMessageTraceRepository extends JpaRepository<MessageTraceJpaEntity, UUID> {}
