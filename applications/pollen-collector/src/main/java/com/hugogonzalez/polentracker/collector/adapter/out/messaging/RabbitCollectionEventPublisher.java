package com.hugogonzalez.polentracker.collector.adapter.out.messaging;

import com.hugogonzalez.polentracker.collector.application.port.out.CollectionEventPublisher;
import com.hugogonzalez.polentracker.messaging.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.slf4j.*;

@Component
public class RabbitCollectionEventPublisher implements CollectionEventPublisher {
  private static final Logger log = LoggerFactory.getLogger(RabbitCollectionEventPublisher.class);
  private final RabbitTemplate rabbit;

  public RabbitCollectionEventPublisher(RabbitTemplate rabbit) {
    this.rabbit = rabbit;
  }

  public void measurement(PollenMeasurementCollected e) {
    rabbit.convertAndSend(RabbitTopology.EXCHANGE, RabbitTopology.MEASUREMENT, e);
    log.debug("Pollen measurement event published event_id={} logical_key={}", e.eventId(), e.logicalKey());
  }

  public void completed(CollectionCompleted e) {
    rabbit.convertAndSend(RabbitTopology.EXCHANGE, RabbitTopology.COMPLETED, e);
    log.info("Collection completed event published event_id={} produced_items={}", e.eventId(), e.producedItems());
  }

  public void failed(CollectionFailed e) {
    rabbit.convertAndSend(RabbitTopology.EXCHANGE, RabbitTopology.FAILED, e);
    log.warn("Collection failed event published event_id={} error_code={}", e.eventId(), e.errorCode());
  }
}
