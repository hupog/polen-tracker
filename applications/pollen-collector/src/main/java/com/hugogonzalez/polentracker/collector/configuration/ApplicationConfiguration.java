package com.hugogonzalez.polentracker.collector.configuration;
import com.hugogonzalez.polentracker.collector.application.port.out.*; import com.hugogonzalez.polentracker.collector.application.service.*; import org.springframework.context.annotation.*; import java.util.List;
@Configuration public class ApplicationConfiguration {
 @Bean PollenSourceRegistry pollenSourceRegistry(List<PollenSourcePort> sources){return new PollenSourceRegistry(sources);}
 @Bean CollectionProcessingService collectionProcessingService(PollenSourceRegistry sources,CollectionEventPublisher events){return new CollectionProcessingService(sources,events);}
}
