package com.rkd.auto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.ZonedDateTime;

import static com.rkd.auto.definition.MessageDefinition.Vehicle.*;

@Schema(description = "Represents a vehicle movement event sent to the webhook.")
public record VehicleRequest(

        @Schema(
                description = "License plate of the vehicle",
                example = "ZUL0001"
        )
        @NotBlank(message = LICENSE_PLATE_CANNOT_BLANK)
        @JsonProperty("license_plate")
        String licensePlate,

        @Schema(
                description = "Type of event: ENTRY, PARKED, or EXIT",
                example = "ENTRY"
        )
        @NotBlank(message = EVENT_TYPE_CANNOT_BLANK)
        @JsonProperty("event_type")
        String eventType,

        @Schema(
                description = "Timestamp when the vehicle entered the garage",
                example = "2025-01-01T12:00:00Z",
                nullable = true
        )
        @JsonProperty("entry_time")
        ZonedDateTime entryTime,

        @Schema(
                description = "Timestamp when the vehicle exited the garage",
                example = "2025-01-01T15:00:00Z",
                nullable = true
        )
        @JsonProperty("exit_time")
        ZonedDateTime exitTime,

        @Schema(
                description = "Latitude where the vehicle is located",
                example = "-23.561684"
        )
        @NotNull(message = LAT_CANNOT_NULL)
        Double lat,

        @Schema(
                description = "Longitude where the vehicle is located",
                example = "-46.655981"
        )
        @NotNull(message = LNG_CANNOT_NULL)
        Double lng
) {
}
