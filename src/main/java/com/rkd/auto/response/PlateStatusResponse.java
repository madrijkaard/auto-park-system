package com.rkd.auto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;

public record PlateStatusResponse(
        @JsonProperty("license_plate")
        String licensePlate,
        @JsonProperty("price_until_now")
        double priceUntilNow,
        @JsonProperty("entry_time")
        ZonedDateTime entryTime,
        @JsonProperty("time_parked")
        ZonedDateTime timeParked,
        Double lat,
        Double lng
) {
}