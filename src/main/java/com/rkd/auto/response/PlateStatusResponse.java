package com.rkd.auto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.ZonedDateTime;

@Schema(description = "Response containing the current status of a vehicle identified by its license plate.")
public record PlateStatusResponse(
        @Schema(description = "License plate of the vehicle", example = "ZUL0001")
        @JsonProperty("license_plate")
        String licensePlate,

        @Schema(description = "Accumulated price until now", example = "15.75")
        @JsonProperty("price_until_now")
        double priceUntilNow,

        @Schema(description = "Timestamp of garage entry", example = "2025-01-01T12:00:00Z")
        @JsonProperty("entry_time")
        ZonedDateTime entryTime,

        @Schema(description = "Timestamp of when the vehicle was parked", example = "2025-01-01T12:15:00Z")
        @JsonProperty("time_parked")
        ZonedDateTime timeParked,

        @Schema(description = "Current latitude of the vehicle", example = "-23.561684")
        Double lat,

        @Schema(description = "Current longitude of the vehicle", example = "-46.655981")
        Double lng
) {
}
