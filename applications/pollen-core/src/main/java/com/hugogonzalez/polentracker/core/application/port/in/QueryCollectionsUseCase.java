package com.hugogonzalez.polentracker.core.application.port.in;

import com.hugogonzalez.polentracker.core.domain.model.PollenCollection;
import java.util.*;

public interface QueryCollectionsUseCase {
  List<PollenCollection> findAll();

  Optional<PollenCollection> find(UUID id);
}
