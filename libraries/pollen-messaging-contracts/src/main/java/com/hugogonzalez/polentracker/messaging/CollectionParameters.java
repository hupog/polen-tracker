package com.hugogonzalez.polentracker.messaging;
import com.hugogonzalez.polentracker.domain.GeographicLocation;
import java.time.LocalDate;
public record CollectionParameters(LocalDate dateFrom, LocalDate dateTo, GeographicLocation location) {}
