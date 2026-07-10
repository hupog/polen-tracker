package com.hugogonzalez.polentracker.core.application.port.out;
import com.hugogonzalez.polentracker.core.domain.model.PollenCollection;
import java.util.*;
public interface CollectionStore { PollenCollection save(PollenCollection collection); List<PollenCollection> findAll(); Optional<PollenCollection> find(UUID id); }
