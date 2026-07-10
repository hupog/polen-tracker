package com.hugogonzalez.polentracker.core.application.port.in;
import com.hugogonzalez.polentracker.core.application.model.StartCollectionCommand;
import com.hugogonzalez.polentracker.core.domain.model.PollenCollection;
public interface StartCollectionUseCase { PollenCollection start(StartCollectionCommand command); }
