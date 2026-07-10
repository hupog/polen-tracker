package com.hugogonzalez.polentracker.core.adapter.out.persistence.trace;

import com.hugogonzalez.polentracker.core.application.model.MessageTrace;
import com.hugogonzalez.polentracker.core.application.port.out.MessageTraceStore;
import org.springframework.stereotype.Component;

@Component
public class JpaMessageTraceStore implements MessageTraceStore {
  private final SpringDataMessageTraceRepository repository;

  public JpaMessageTraceStore(SpringDataMessageTraceRepository repository) {
    this.repository = repository;
  }

  public void save(MessageTrace trace) {
    repository.save(new MessageTraceJpaEntity(trace));
  }
}
