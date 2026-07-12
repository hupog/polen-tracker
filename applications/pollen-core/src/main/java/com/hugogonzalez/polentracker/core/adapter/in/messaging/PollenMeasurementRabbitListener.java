package com.hugogonzalez.polentracker.core.adapter.in.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hugogonzalez.polentracker.core.adapter.out.messaging.RabbitTopology;
import com.hugogonzalez.polentracker.core.application.model.MessageTrace;
import com.hugogonzalez.polentracker.core.application.port.in.StorePollenMeasurementUseCase;
import com.hugogonzalez.polentracker.core.application.port.out.MessageTraceStore;
import com.hugogonzalez.polentracker.messaging.PollenMeasurementCollected;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.slf4j.*;

@Component
public class PollenMeasurementRabbitListener {
  private static final Logger log = LoggerFactory.getLogger(PollenMeasurementRabbitListener.class);
  private final StorePollenMeasurementUseCase measurements;
  private final MessageTraceStore traces;
  private final ObjectMapper mapper;

  public PollenMeasurementRabbitListener(
      StorePollenMeasurementUseCase measurements, MessageTraceStore traces, ObjectMapper mapper) {
    this.measurements = measurements;
    this.traces = traces;
    this.mapper = mapper;
  }

  @RabbitListener(queues = RabbitTopology.MEASUREMENT_QUEUE)
  public void handle(PollenMeasurementCollected event, Message message) {
    try (var request = MDC.putCloseable("requestId", event.requestId().toString());
        var collection = MDC.putCloseable("collectionId", event.requestId().toString());
        var messageId = MDC.putCloseable("messageId", event.eventId().toString())) {
      log.info("Pollen measurement message received redelivered={}", message.getMessageProperties().getRedelivered());
      measurements.store(event);
      trace(event, message);
      log.info("Pollen measurement message processed");
    }
  }

  private void trace(PollenMeasurementCollected event, Message message) {
    try {
      var properties = message.getMessageProperties();
      traces.save(
          new MessageTrace(
              UUID.randomUUID(),
              event.requestId(),
              event.eventId(),
              "INBOUND",
              event.getClass().getName(),
              "RECEIVED",
              properties.getReceivedExchange(),
              properties.getReceivedRoutingKey(),
              RabbitTopology.MEASUREMENT_QUEUE,
              properties.getDeliveryTag(),
              properties.getConsumerTag(),
              properties.getCorrelationId(),
              Boolean.TRUE.equals(properties.getRedelivered()),
              mapper.writeValueAsString(properties.getHeaders()),
              new String(message.getBody(), StandardCharsets.UTF_8),
              Instant.now()));
    } catch (Exception e) {
      throw new IllegalStateException("Rabbit message trace could not be stored", e);
    }
  }
}
