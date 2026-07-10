package com.hugogonzalez.polentracker.collector.application.port.in;
import com.hugogonzalez.polentracker.messaging.CollectionRequest;
public interface ProcessCollectionUseCase { void process(CollectionRequest request); }
