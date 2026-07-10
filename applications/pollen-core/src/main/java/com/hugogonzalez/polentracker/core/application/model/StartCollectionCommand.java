package com.hugogonzalez.polentracker.core.application.model;

import com.hugogonzalez.polentracker.domain.GeographicLocation;
import com.hugogonzalez.polentracker.domain.PollenSourceType;
import java.time.LocalDate;

public record StartCollectionCommand(PollenSourceType sourceType, LocalDate dateFrom, LocalDate dateTo,
                                     GeographicLocation location) {}
