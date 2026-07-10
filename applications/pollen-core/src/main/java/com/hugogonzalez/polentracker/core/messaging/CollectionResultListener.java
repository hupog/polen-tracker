package com.hugogonzalez.polentracker.core.messaging;

import com.hugogonzalez.polentracker.core.collection.CollectionRepository;
import com.hugogonzalez.polentracker.core.config.MessagingConfig;
import com.hugogonzalez.polentracker.messaging.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.amqp.core.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
public class CollectionResultListener {
    private final CollectionRepository repository;
    private final RabbitMessageTraceRepository traceRepository;
    private final ObjectMapper objectMapper;

    public CollectionResultListener(CollectionRepository r, RabbitMessageTraceRepository traceRepository, ObjectMapper objectMapper) {
        repository = r;
        this.traceRepository = traceRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    @RabbitListener(queues = MessagingConfig.COMPLETED_QUEUE)
    public void completed(CollectionCompleted event, Message message) {
        trace(event.requestId(), event.eventId(), event, message, MessagingConfig.COMPLETED_QUEUE);
        repository.findById(event.requestId()).ifPresent(c -> c.complete(event.producedItems(), event.completedAt()));
    }

    @Transactional
    @RabbitListener(queues = MessagingConfig.FAILED_QUEUE)
    public void failed(CollectionFailed event, Message message) {
        trace(event.requestId(), event.eventId(), event, message, MessagingConfig.FAILED_QUEUE);
        repository.findById(event.requestId()).ifPresent(c -> c.fail(event.failedAt()));
    }

    private void trace(UUID collectionId, UUID messageId, Object event, Message message, String queue) {
        try {
            var p = message.getMessageProperties();
            traceRepository.save(new RabbitMessageTraceEntity(collectionId, messageId, "INBOUND", event.getClass().getName(),
                    "RECEIVED", p.getReceivedExchange(), p.getReceivedRoutingKey(), queue, p.getDeliveryTag(),
                    p.getConsumerTag(), p.getCorrelationId(), Boolean.TRUE.equals(p.getRedelivered()),
                    objectMapper.writeValueAsString(p.getHeaders()), new String(message.getBody(), StandardCharsets.UTF_8)));
        } catch (Exception e) { throw new IllegalStateException("No se pudo registrar el mensaje Rabbit", e); }
    }
}
