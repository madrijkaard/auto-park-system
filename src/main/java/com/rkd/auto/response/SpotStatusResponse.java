package com.rkd.auto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;

public record SpotStatusResponse(
        boolean ocupied,
        @JsonProperty("license_plate")
        String licensePlate,
        @JsonProperty("price_until_now")
        double priceUntilNow,
        @JsonProperty("entry_time")
        ZonedDateTime entryTime,
        @JsonProperty("parked_time")
        ZonedDateTime timeParked
) {
}
