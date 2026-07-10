package com.hugogonzalez.polentracker.core.configuration;

import com.hugogonzalez.polentracker.core.application.port.out.*;
import com.hugogonzalez.polentracker.core.application.service.*;
import org.springframework.context.annotation.*;

@Configuration
public class ApplicationConfiguration {
  @Bean
  CollectionApplicationService collectionApplicationService(
      CollectionStore store, CollectionRequestPublisher publisher) {
    return new CollectionApplicationService(store, publisher);
  }

  @Bean
  PollenMeasurementApplicationService pollenMeasurementApplicationService(
      PollenMeasurementStore store) {
    return new PollenMeasurementApplicationService(store);
  }
}
