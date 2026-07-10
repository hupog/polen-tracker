package com.hugogonzalez.polentracker.collector.source;

import com.hugogonzalez.polentracker.domain.*;
import com.hugogonzalez.polentracker.messaging.CollectionParameters;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OpenMeteoSourceAdapter implements PollenSourceAdapter {
    public PollenSourceType supportedSource() {
        return PollenSourceType.OPEN_METEO;
    }

    public List<PollenMeasurement> collect(CollectionParameters parameters) {/* El cliente y el mapeo de Open-Meteo se implementan en la siguiente iteración. */
        return List.of();
    }
}
