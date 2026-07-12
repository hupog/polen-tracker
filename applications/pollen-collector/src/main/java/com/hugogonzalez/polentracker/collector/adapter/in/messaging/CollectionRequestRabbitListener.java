package com.hugogonzalez.polentracker.collector.adapter.in.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hugogonzalez.polentracker.collector.adapter.out.messaging.RabbitTopology;
import com.hugogonzalez.polentracker.collector.application.model.MessageTrace;
import com.hugogonzalez.polentracker.collector.application.port.in.ProcessCollectionUseCase;
import com.hugogonzalez.polentracker.collector.application.port.out.MessageTraceStore;
import com.hugogonzalez.polentracker.messaging.CollectionRequest;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.slf4j.*;

@Component
public class CollectionRequestRabbitListener {
  private static final Logger log = LoggerFactory.getLogger(CollectionRequestRabbitListener.class);
  private final ProcessCollectionUseCase processor;
  private final MessageTraceStore traces;
  private final ObjectMapper mapper;

  public CollectionRequestRabbitListener(
      ProcessCollectionUseCase processor, MessageTraceStore traces, ObjectMapper mapper) {
    this.processor = processor;
    this.traces = traces;
    this.mapper = mapper;
  }

  @RabbitListener(queues = RabbitTopology.REQUEST_QUEUE)
  public void handle(CollectionRequest request, Message message) {
    var rabbitMessageId = message.getMessageProperties().getMessageId();
    try (var requestContext = MDC.putCloseable("requestId", request.requestId().toString());
        var collection = MDC.putCloseable("collectionId", request.requestId().toString());
        var messageContext = MDC.putCloseable("messageId", String.valueOf(rabbitMessageId))) {
      log.info(
          "Collection request received source={} reason={} redelivered={}",
          request.sourceType(),
          request.reason(),
          message.getMessageProperties().getRedelivered());
    try {
      var p = message.getMessageProperties();
      traces.save(
          new MessageTrace(
              UUID.randomUUID(),
              request.requestId(),
              parse(p.getMessageId()),
              "INBOUND",
              request.getClass().getName(),
              "RECEIVED",
              p.getReceivedExchange(),
              p.getReceivedRoutingKey(),
              RabbitTopology.REQUEST_QUEUE,
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
    processor.process(request);
      log.info("Collection request processed");
    }
  }

  private UUID parse(String value) {
    try {
      return value == null ? null : UUID.fromString(value);
    } catch (IllegalArgumentException ignored) {
      return null;
    }
  }
}
