package com.hugogonzalez.polentracker.core.adapter.in.web;
import com.hugogonzalez.polentracker.domain.PollenSourceType;
import jakarta.validation.Valid; import jakarta.validation.constraints.*; import java.time.LocalDate;
public record StartCollectionRequest(@NotNull PollenSourceType sourceType, @NotNull LocalDate dateFrom,
                                     @NotNull LocalDate dateTo, @Valid @NotNull Location location) {
    public record Location(String name, @DecimalMin("-90") @DecimalMax("90") double latitude,
                           @DecimalMin("-180") @DecimalMax("180") double longitude) {}
}
