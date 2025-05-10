package com.rkd.auto.response;

import java.time.ZonedDateTime;

public record PlateStatusResponse(
        String license_plate,
        double price_until_now,
        ZonedDateTime entry_time,
        ZonedDateTime time_parked,
        Double lat,
        Double lng
) {
}