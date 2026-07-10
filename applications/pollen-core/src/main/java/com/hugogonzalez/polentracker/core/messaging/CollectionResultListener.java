package com.hugogonzalez.polentracker.core.messaging;

import com.hugogonzalez.polentracker.core.collection.CollectionRepository;
import com.hugogonzalez.polentracker.core.config.MessagingConfig;
import com.hugogonzalez.polentracker.messaging.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CollectionResultListener {
    private final CollectionRepository repository;

    public CollectionResultListener(CollectionRepository r) {
        repository = r;
    }

    @Transactional
    @RabbitListener(queues = MessagingConfig.COMPLETED_QUEUE)
    public void completed(CollectionCompleted event) {
        repository.findById(event.requestId()).ifPresent(c -> c.complete(event.producedItems(), event.completedAt()));
    }

    @Transactional
    @RabbitListener(queues = MessagingConfig.FAILED_QUEUE)
    public void failed(CollectionFailed event) {
        repository.findById(event.requestId()).ifPresent(c -> c.fail(event.failedAt()));
    }
}
