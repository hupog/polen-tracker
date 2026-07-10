package com.hugogonzalez.polentracker.core.collection;

import com.hugogonzalez.polentracker.core.config.MessagingConfig;
import com.hugogonzalez.polentracker.domain.GeographicLocation;
import com.hugogonzalez.polentracker.messaging.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
public class CollectionService {
    private final CollectionRepository repository;
    private final RabbitTemplate rabbit;

    public CollectionService(CollectionRepository r, RabbitTemplate rabbit) {
        this.repository = r;
        this.rabbit = rabbit;
    }

    @Transactional
    public CollectionEntity start(StartCollectionCommand c) {
        UUID id = UUID.randomUUID();
        CollectionEntity entity = repository.save(new CollectionEntity(id, c.sourceType()));
        var l = c.location();
        var p = new CollectionParameters(c.dateFrom(), c.dateTo(), new GeographicLocation(l.name(), l.latitude(), l.longitude()));
        rabbit.convertAndSend(MessagingConfig.EXCHANGE, "collection.requested", new CollectionRequest("1.0", id, Instant.now(), CollectionReason.MANUAL, c.sourceType(), p));
        return entity;
    }

    public List<CollectionEntity> findAll() {
        return repository.findAll();
    }

    public Optional<CollectionEntity> find(UUID id) {
        return repository.findById(id);
    }
}
