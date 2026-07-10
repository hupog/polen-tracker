package com.hugogonzalez.polentracker.core.collection;

import com.hugogonzalez.polentracker.core.config.MessagingConfig;
import com.hugogonzalez.polentracker.domain.GeographicLocation;
import com.hugogonzalez.polentracker.messaging.*;
import com.hugogonzalez.polentracker.core.messaging.RabbitMessageTraceEntity;
import com.hugogonzalez.polentracker.core.messaging.RabbitMessageTraceRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.*;

@Service
public class CollectionService {
    private final CollectionRepository repository;
    private final RabbitTemplate rabbit;
    private final RabbitMessageTraceRepository traceRepository;
    private final ObjectMapper objectMapper;

    public CollectionService(CollectionRepository r, RabbitTemplate rabbit, RabbitMessageTraceRepository traceRepository,
                             ObjectMapper objectMapper) {
        this.repository = r;
        this.rabbit = rabbit;
        this.traceRepository = traceRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public CollectionEntity start(StartCollectionCommand c) {
        UUID id = UUID.randomUUID();
        CollectionEntity entity = repository.save(new CollectionEntity(id, c.sourceType()));
        var l = c.location();
        var p = new CollectionParameters(c.dateFrom(), c.dateTo(), new GeographicLocation(l.name(), l.latitude(), l.longitude()));
        var request = new CollectionRequest("1.0", id, Instant.now(), CollectionReason.MANUAL, c.sourceType(), p);
        rabbit.convertAndSend(MessagingConfig.EXCHANGE, "collection.requested", request, message -> {
            try {
                var messageId = UUID.randomUUID();
                message.getMessageProperties().setMessageId(messageId.toString());
                traceRepository.save(new RabbitMessageTraceEntity(id, messageId, "OUTBOUND", request.getClass().getName(),
                        "PUBLISHED", MessagingConfig.EXCHANGE, "collection.requested", null, null, null,
                        message.getMessageProperties().getCorrelationId(), false, "{}", objectMapper.writeValueAsString(request)));
                return message;
            } catch (Exception e) { throw new IllegalStateException("No se pudo registrar el mensaje Rabbit", e); }
        });
        return entity;
    }

    public List<CollectionEntity> findAll() {
        return repository.findAll();
    }

    public Optional<CollectionEntity> find(UUID id) {
        return repository.findById(id);
    }
}
