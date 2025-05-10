package com.rkd.auto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;

public record VehicleRequest(
        @JsonProperty("license_plate") String licensePlate,
        @JsonProperty("event_type") String eventType,
        @JsonProperty("entry_time") ZonedDateTime entryTime,
        @JsonProperty("exit_time") ZonedDateTime exitTime,
        Double lat,
        Double lng
) {
}