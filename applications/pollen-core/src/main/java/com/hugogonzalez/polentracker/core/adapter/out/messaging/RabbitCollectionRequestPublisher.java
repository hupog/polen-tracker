package com.hugogonzalez.polentracker.core.adapter.out.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hugogonzalez.polentracker.core.application.model.MessageTrace;
import com.hugogonzalez.polentracker.core.application.port.out.*;
import com.hugogonzalez.polentracker.messaging.CollectionRequest;
import java.time.Instant;
import java.util.UUID;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitCollectionRequestPublisher implements CollectionRequestPublisher {
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
    rabbit.convertAndSend(
        RabbitTopology.EXCHANGE,
        RabbitTopology.REQUESTED,
        request,
        message -> {
          try {
            var messageId = UUID.randomUUID();
            message.getMessageProperties().setMessageId(messageId.toString());
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
            throw new IllegalStateException("No se pudo registrar el mensaje Rabbit", e);
          }
        });
  }
}
