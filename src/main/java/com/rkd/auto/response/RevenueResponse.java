package com.rkd.auto.response;

import java.time.ZonedDateTime;

public record RevenueResponse(
        double amount,
        String currency,
        ZonedDateTime timestamp
) {
    public RevenueResponse(double amount, ZonedDateTime timestamp) {
        this(amount, "BRL", timestamp);
    }
}
