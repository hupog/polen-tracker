package com.hugogonzalez.polentracker.core.adapter.out.messaging;

public final class RabbitTopology {
  public static final String EXCHANGE = "pollen.events",
      DLX = "pollen.dlx",
      REQUEST_QUEUE = "collection.requests",
      MEASUREMENT_QUEUE = "collection.measurements.core",
      COMPLETED_QUEUE = "collection.completed.core",
      FAILED_QUEUE = "collection.failed.core";
  public static final String REQUESTED = "collection.requested",
      MEASUREMENT = "collection.measurement-collected",
      COMPLETED = "collection.completed",
      FAILED = "collection.failed";

  private RabbitTopology() {}
}
