package com.hugogonzalez.polentracker.core.adapter.out.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hugogonzalez.polentracker.core.application.model.MessageTrace;
import com.hugogonzalez.polentracker.core.application.port.out.*;
import com.hugogonzalez.polentracker.messaging.CollectionRequest;
import java.time.Instant;
import java.util.UUID;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.slf4j.*;

@Component
public class RabbitCollectionRequestPublisher implements CollectionRequestPublisher {
  private static final Logger log = LoggerFactory.getLogger(RabbitCollectionRequestPublisher.class);
  private final RabbitTemplate rabbit;
  private final MessageTraceStore traces;
  private final ObjectMapper mapper;

  public RabbitCollectionRequestPublisher(
      RabbitTemplate rabbit, MessageTraceStore traces, ObjectMapper mapper) {
    this.rabbit = rabbit;
    this.traces = traces;
    this.mapper = mapper;
  }

  public void publish(CollectionRequest request) {
    log.info(
        "Publishing collection request exchange={} routing_key={} source={}",
        RabbitTopology.EXCHANGE,
        RabbitTopology.REQUESTED,
        request.sourceType());
    rabbit.convertAndSend(
        RabbitTopology.EXCHANGE,
        RabbitTopology.REQUESTED,
        request,
        message -> {
          try {
            var messageId = UUID.randomUUID();
            message.getMessageProperties().setMessageId(messageId.toString());
            log.debug("Rabbit message created message_id={}", messageId);
            traces.save(
                new MessageTrace(
                    UUID.randomUUID(),
                    request.requestId(),
                    messageId,
                    "OUTBOUND",
                    request.getClass().getName(),
                    "PUBLISHED",
                    RabbitTopology.EXCHANGE,
                    RabbitTopology.REQUESTED,
                    null,
                    null,
                    null,
                    message.getMessageProperties().getCorrelationId(),
                    false,
                    "{}",
                    mapper.writeValueAsString(request),
                    Instant.now()));
            return message;
          } catch (Exception e) {
            throw new IllegalStateException("Rabbit message trace could not be stored", e);
          }
        });
    log.info("Collection request handed to RabbitTemplate");
  }
}
