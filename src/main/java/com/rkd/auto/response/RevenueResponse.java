package com.rkd.auto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.ZonedDateTime;

@Schema(description = "Response containing the calculated revenue for a specific date and sector.")
public record RevenueResponse(

        @Schema(description = "Total revenue amount", example = "38.50")
        double amount,

        @Schema(description = "Currency of the amount", example = "BRL")
        String currency,

        @Schema(description = "Timestamp of the revenue calculation", example = "2025-01-01T23:59:59Z")
        ZonedDateTime timestamp

) {
    public RevenueResponse(double amount, ZonedDateTime timestamp) {
        this(amount, "BRL", timestamp);
    }
}
