package com.hugogonzalez.polentracker.collector.application.service;

import com.hugogonzalez.polentracker.collector.application.port.out.PollenSourcePort;
import com.hugogonzalez.polentracker.domain.PollenSourceType;
import java.util.*;

public class PollenSourceRegistry {
  private final Map<PollenSourceType, PollenSourcePort> sources;

  public PollenSourceRegistry(List<PollenSourcePort> values) {
    var map = new EnumMap<PollenSourceType, PollenSourcePort>(PollenSourceType.class);
    values.forEach(v -> map.put(v.supportedSource(), v));
    sources = Map.copyOf(map);
  }

  public PollenSourcePort get(PollenSourceType type) {
    var source = sources.get(type);
    if (source == null) throw new IllegalArgumentException("Unsupported source: " + type);
    return source;
  }
}
