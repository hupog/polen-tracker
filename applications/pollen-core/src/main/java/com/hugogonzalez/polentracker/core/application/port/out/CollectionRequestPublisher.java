package com.hugogonzalez.polentracker.core.application.port.out;
import com.hugogonzalez.polentracker.messaging.CollectionRequest;
public interface CollectionRequestPublisher { void publish(CollectionRequest request); }
