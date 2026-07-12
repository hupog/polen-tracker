package com.hugogonzalez.polentracker.core.adapter.in.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hugogonzalez.polentracker.core.adapter.out.messaging.RabbitTopology;
import com.hugogonzalez.polentracker.core.application.model.MessageTrace;
import com.hugogonzalez.polentracker.core.application.port.in.HandleCollectionResultUseCase;
import com.hugogonzalez.polentracker.core.application.port.out.MessageTraceStore;
import com.hugogonzalez.polentracker.messaging.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.slf4j.*;

@Component
public class CollectionResultRabbitListener {
  private static final Logger log = LoggerFactory.getLogger(CollectionResultRabbitListener.class);
  private final HandleCollectionResultUseCase results;
  private final MessageTraceStore traces;
  private final ObjectMapper mapper;

  public CollectionResultRabbitListener(
      HandleCollectionResultUseCase results, MessageTraceStore traces, ObjectMapper mapper) {
    this.results = results;
    this.traces = traces;
    this.mapper = mapper;
  }

  @RabbitListener(queues = RabbitTopology.COMPLETED_QUEUE)
  public void completed(CollectionCompleted event, Message message) {
    try (var request = MDC.putCloseable("requestId", event.requestId().toString());
        var collection = MDC.putCloseable("collectionId", event.requestId().toString());
        var messageId = MDC.putCloseable("messageId", event.eventId().toString())) {
      log.info("Collection completion message received produced_items={}", event.producedItems());
      trace(event.requestId(), event.eventId(), event, message, RabbitTopology.COMPLETED_QUEUE);
      results.complete(event.requestId(), event.producedItems(), event.completedAt());
      log.info("Collection completion message processed");
    }
  }

  @RabbitListener(queues = RabbitTopology.FAILED_QUEUE)
  public void failed(CollectionFailed event, Message message) {
    try (var request = MDC.putCloseable("requestId", event.requestId().toString());
        var collection = MDC.putCloseable("collectionId", event.requestId().toString());
        var messageId = MDC.putCloseable("messageId", event.eventId().toString())) {
      log.warn("Collection failure message received error_code={}", event.errorCode());
      trace(event.requestId(), event.eventId(), event, message, RabbitTopology.FAILED_QUEUE);
      results.fail(event.requestId(), event.failedAt());
      log.info("Collection failure message processed");
    }
  }

  private void trace(
      UUID collectionId, UUID messageId, Object event, Message message, String queue) {
    try {
      var p = message.getMessageProperties();
      traces.save(
          new MessageTrace(
              UUID.randomUUID(),
              collectionId,
              messageId,
              "INBOUND",
              event.getClass().getName(),
              "RECEIVED",
              p.getReceivedExchange(),
              p.getReceivedRoutingKey(),
              queue,
              p.getDeliveryTag(),
              p.getConsumerTag(),
              p.getCorrelationId(),
              Boolean.TRUE.equals(p.getRedelivered()),
              mapper.writeValueAsString(p.getHeaders()),
              new String(message.getBody(), StandardCharsets.UTF_8),
              Instant.now()));
    } catch (Exception e) {
      throw new IllegalStateException("Rabbit message trace could not be stored", e);
    }
  }
}
