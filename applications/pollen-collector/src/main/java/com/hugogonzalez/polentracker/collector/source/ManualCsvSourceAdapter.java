package com.hugogonzalez.polentracker.collector.source;
import com.hugogonzalez.polentracker.domain.*; import com.hugogonzalez.polentracker.messaging.CollectionParameters; import org.springframework.stereotype.Component; import java.util.List;
@Component public class ManualCsvSourceAdapter implements PollenSourceAdapter {public PollenSourceType supportedSource(){return PollenSourceType.MANUAL_CSV;} public List<PollenMeasurement> collect(CollectionParameters parameters){return List.of();}}
