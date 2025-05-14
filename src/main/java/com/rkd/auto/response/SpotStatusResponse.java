package com.rkd.auto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.ZonedDateTime;

@Schema(description = "Response containing information about a parking spot status.")
public record SpotStatusResponse(
        @Schema(description = "Whether the spot is currently occupied", example = "true")
        boolean ocupied,

        @Schema(description = "License plate of the vehicle occupying the spot", example = "ZUL0001")
        @JsonProperty("license_plate")
        String licensePlate,

        @Schema(description = "Accumulated price until now", example = "12.50")
        @JsonProperty("price_until_now")
        double priceUntilNow,

        @Schema(description = "Timestamp when the vehicle entered the garage", example = "2025-01-01T12:00:00Z")
        @JsonProperty("entry_time")
        ZonedDateTime entryTime,

        @Schema(description = "Timestamp when the vehicle parked in the spot", example = "2025-01-01T12:15:00Z")
        @JsonProperty("parked_time")
        ZonedDateTime timeParked
) {
}
