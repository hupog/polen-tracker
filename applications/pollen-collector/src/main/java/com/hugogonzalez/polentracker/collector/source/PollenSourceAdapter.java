package com.hugogonzalez.polentracker.collector.source; import com.hugogonzalez.polentracker.domain.*; import com.hugogonzalez.polentracker.messaging.CollectionParameters; import java.util.List;
public interface PollenSourceAdapter {PollenSourceType supportedSource(); List<PollenMeasurement> collect(CollectionParameters parameters);}
