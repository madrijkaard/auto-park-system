package com.rkd.auto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.ZonedDateTime;

import static com.rkd.auto.definition.MessageDefinition.Vehicle.*;

public record VehicleRequest(
        @NotBlank(message = LICENSE_PLATE_CANNOT_BLANK)
        @JsonProperty("license_plate") String licensePlate,
        @NotBlank(message = EVENT_TYPE_CANNOT_BLANK)
        @JsonProperty("event_type") String eventType,
        @JsonProperty("entry_time") ZonedDateTime entryTime,
        @JsonProperty("exit_time") ZonedDateTime exitTime,
        @NotNull(message = LAT_CANNOT_NULL)
        Double lat,
        @NotNull(message = LNG_CANNOT_NULL)
        Double lng
) {
}