package com.hugogonzalez.polentracker.collector.adapter.out.source.csv;

import com.hugogonzalez.polentracker.collector.application.port.out.PollenSourcePort;
import com.hugogonzalez.polentracker.domain.*;
import com.hugogonzalez.polentracker.messaging.CollectionParameters;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ManualCsvSourceAdapter implements PollenSourcePort {
  public PollenSourceType supportedSource() {
    return PollenSourceType.MANUAL_CSV;
  }

  public List<PollenMeasurement> collect(CollectionParameters parameters) {
    return List.of();
  }
}
