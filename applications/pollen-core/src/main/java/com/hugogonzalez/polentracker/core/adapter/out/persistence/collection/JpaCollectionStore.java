package com.hugogonzalez.polentracker.core.adapter.out.persistence.collection;
import com.hugogonzalez.polentracker.core.application.port.out.CollectionStore; import com.hugogonzalez.polentracker.core.domain.model.PollenCollection; import org.springframework.stereotype.Component; import java.util.*;
@Component
public class JpaCollectionStore implements CollectionStore {
 private final SpringDataCollectionRepository repository;
 public JpaCollectionStore(SpringDataCollectionRepository repository){this.repository=repository;}
 public PollenCollection save(PollenCollection c){return repository.save(new CollectionJpaEntity(c)).toDomain();}
 public List<PollenCollection> findAll(){return repository.findAll().stream().map(CollectionJpaEntity::toDomain).toList();}
 public Optional<PollenCollection> find(UUID id){return repository.findById(id).map(CollectionJpaEntity::toDomain);}
}
