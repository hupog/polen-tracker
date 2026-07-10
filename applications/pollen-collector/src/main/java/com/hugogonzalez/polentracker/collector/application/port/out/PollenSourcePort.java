package com.hugogonzalez.polentracker.collector.application.port.out;

import com.hugogonzalez.polentracker.domain.*;
import com.hugogonzalez.polentracker.messaging.CollectionParameters;
import java.util.List;

public interface PollenSourcePort {
  PollenSourceType supportedSource();

  List<PollenMeasurement> collect(CollectionParameters parameters);
}
