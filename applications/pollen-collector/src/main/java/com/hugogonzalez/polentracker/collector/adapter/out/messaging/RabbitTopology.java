package com.hugogonzalez.polentracker.collector.adapter.out.messaging;

public final class RabbitTopology {
  public static final String EXCHANGE = "pollen.events",
      DLX = "pollen.dlx",
      REQUEST_QUEUE = "collection.requests",
      REQUESTED = "collection.requested",
      MEASUREMENT = "collection.measurement-collected",
      COMPLETED = "collection.completed",
      FAILED = "collection.failed";

  private RabbitTopology() {}
}
