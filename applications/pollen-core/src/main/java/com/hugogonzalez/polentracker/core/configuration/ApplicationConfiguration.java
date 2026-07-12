package com.hugogonzalez.polentracker.core.configuration;

import com.hugogonzalez.polentracker.core.application.port.out.*;
import com.hugogonzalez.polentracker.core.application.service.*;
import java.time.Clock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableScheduling
public class ApplicationConfiguration {
  @Bean
  Clock clock() {
    return Clock.systemUTC();
  }

  @Bean("outboxExecutor")
  ThreadPoolTaskExecutor outboxExecutor(
      @Value("${messaging.outbox.workers:4}") int workers,
      @Value("${messaging.outbox.queue-capacity:20}") int queueCapacity) {
    var executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(workers);
    executor.setMaxPoolSize(workers);
    executor.setQueueCapacity(queueCapacity);
    executor.setThreadNamePrefix("outbox-publisher-");
    executor.setWaitForTasksToCompleteOnShutdown(true);
    executor.setAwaitTerminationSeconds(20);
    return executor;
  }

  @Bean
  CollectionApplicationService collectionApplicationService(
      CollectionStore store, CollectionSubmissionStore submissions) {
    return new CollectionApplicationService(store, submissions);
  }

  @Bean
  PollenMeasurementApplicationService pollenMeasurementApplicationService(
      PollenMeasurementStore store) {
    return new PollenMeasurementApplicationService(store);
  }
}
