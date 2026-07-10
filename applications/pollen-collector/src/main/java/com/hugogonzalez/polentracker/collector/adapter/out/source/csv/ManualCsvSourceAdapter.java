package com.hugogonzalez.polentracker.collector.adapter.out.source.csv;
import com.hugogonzalez.polentracker.collector.application.port.out.PollenSourcePort; import com.hugogonzalez.polentracker.domain.*; import com.hugogonzalez.polentracker.messaging.CollectionParameters; import org.springframework.stereotype.Component; import java.util.List;
@Component public class ManualCsvSourceAdapter implements PollenSourcePort {public PollenSourceType supportedSource(){return PollenSourceType.MANUAL_CSV;}public List<PollenMeasurement> collect(CollectionParameters parameters){return List.of();}}
