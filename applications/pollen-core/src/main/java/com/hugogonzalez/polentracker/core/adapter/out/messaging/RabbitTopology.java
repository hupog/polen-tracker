package com.hugogonzalez.polentracker.core.adapter.out.messaging;

public final class RabbitTopology {
  public static final String EXCHANGE = "pollen.events",
      DLX = "pollen.dlx",
      REQUEST_QUEUE = "collection.requests",
      MEASUREMENT_QUEUE = "collection.measurements.core",
      COMPLETED_QUEUE = "collection.completed.core",
      FAILED_QUEUE = "collection.failed.core",
      DEAD_LETTER_QUEUE = "pollen.dead-letter";
  public static final String REQUESTED = "collection.requested",
      MEASUREMENT = "collection.measurement-collected",
      COMPLETED = "collection.completed",
      FAILED = "collection.failed",
      REQUEST_DEAD_LETTER = "collection.requests.failed",
      RESULT_DEAD_LETTER = "collection.results.failed",
      MEASUREMENT_DEAD_LETTER = "collection.measurements.failed";

  private RabbitTopology() {}
}
