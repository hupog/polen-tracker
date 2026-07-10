package com.hugogonzalez.polentracker.collector.adapter.out.messaging;

import com.hugogonzalez.polentracker.collector.application.port.out.CollectionEventPublisher;
import com.hugogonzalez.polentracker.messaging.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitCollectionEventPublisher implements CollectionEventPublisher {
  private final RabbitTemplate rabbit;

  public RabbitCollectionEventPublisher(RabbitTemplate rabbit) {
    this.rabbit = rabbit;
  }

  public void measurement(PollenMeasurementCollected e) {
    rabbit.convertAndSend(RabbitTopology.EXCHANGE, RabbitTopology.MEASUREMENT, e);
  }

  public void completed(CollectionCompleted e) {
    rabbit.convertAndSend(RabbitTopology.EXCHANGE, RabbitTopology.COMPLETED, e);
  }

  public void failed(CollectionFailed e) {
    rabbit.convertAndSend(RabbitTopology.EXCHANGE, RabbitTopology.FAILED, e);
  }
}
