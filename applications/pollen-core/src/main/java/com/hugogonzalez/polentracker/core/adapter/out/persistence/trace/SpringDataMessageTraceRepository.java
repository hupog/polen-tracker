package com.hugogonzalez.polentracker.core.adapter.out.persistence.trace;
import org.springframework.data.jpa.repository.JpaRepository; import java.util.UUID;
interface SpringDataMessageTraceRepository extends JpaRepository<MessageTraceJpaEntity, UUID>{}
