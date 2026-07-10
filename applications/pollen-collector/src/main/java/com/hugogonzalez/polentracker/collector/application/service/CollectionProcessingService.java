package com.hugogonzalez.polentracker.collector.application.service;
import com.hugogonzalez.polentracker.collector.application.port.in.ProcessCollectionUseCase; import com.hugogonzalez.polentracker.collector.application.port.out.CollectionEventPublisher; import com.hugogonzalez.polentracker.messaging.*; import java.time.Instant; import java.util.UUID;
public class CollectionProcessingService implements ProcessCollectionUseCase {
 private final PollenSourceRegistry sources; private final CollectionEventPublisher events;
 public CollectionProcessingService(PollenSourceRegistry sources,CollectionEventPublisher events){this.sources=sources;this.events=events;}
 public void process(CollectionRequest request){
  try{var measurements=sources.get(request.sourceType()).collect(request.params());for(var m:measurements){var id=UUID.randomUUID();var key=request.sourceType()+"|"+m.location().latitude()+"|"+m.location().longitude()+"|"+m.pollenType()+"|"+m.validAt()+"|"+m.metricType();events.measurement(new PollenMeasurementCollected("1.0",id,request.requestId(),request.sourceType(),Instant.now(),key,m));}events.completed(new CollectionCompleted("1.0",UUID.randomUUID(),request.requestId(),measurements.size(),Instant.now()));}
  catch(Exception e){events.failed(new CollectionFailed("1.0",UUID.randomUUID(),request.requestId(),"COLLECTION_FAILED",e.getMessage(),Instant.now()));throw e;}
 }
}
