package com.rkd.auto.response;

import java.time.ZonedDateTime;

public record SpotStatusResponse(
        boolean ocupied,
        String license_plate,
        double price_until_now,
        ZonedDateTime entry_time,
        ZonedDateTime time_parked
) {
}
