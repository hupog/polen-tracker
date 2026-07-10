package com.hugogonzalez.polentracker.collector.messaging;

import com.hugogonzalez.polentracker.collector.config.MessagingConfig;
import com.hugogonzalez.polentracker.messaging.CollectionRequest;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.amqp.core.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
public class CollectionRequestListener {
    private final CollectionProcessor processor;
    private final RabbitMessageTraceRepository traceRepository;
    private final ObjectMapper objectMapper;

    public CollectionRequestListener(CollectionProcessor p, RabbitMessageTraceRepository traceRepository, ObjectMapper objectMapper) {
        processor = p;
        this.traceRepository = traceRepository;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = MessagingConfig.REQUEST_QUEUE)
    public void handle(CollectionRequest request, Message message) {
        try {
            var p = message.getMessageProperties();
            traceRepository.save(new RabbitMessageTraceEntity(request.requestId(), parseMessageId(p.getMessageId()), "INBOUND",
                    request.getClass().getName(), "RECEIVED", p.getReceivedExchange(), p.getReceivedRoutingKey(), MessagingConfig.REQUEST_QUEUE,
                    p.getDeliveryTag(), p.getConsumerTag(), p.getCorrelationId(), Boolean.TRUE.equals(p.getRedelivered()),
                    objectMapper.writeValueAsString(p.getHeaders()), new String(message.getBody(), StandardCharsets.UTF_8)));
        } catch (Exception e) { throw new IllegalStateException("No se pudo registrar el mensaje Rabbit", e); }
        processor.process(request);
    }

    private UUID parseMessageId(String value) {
        try { return value == null ? null : UUID.fromString(value); } catch (IllegalArgumentException ignored) { return null; }
    }
}
