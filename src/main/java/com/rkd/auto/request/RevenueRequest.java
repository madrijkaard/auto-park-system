package com.rkd.auto.request;

import java.time.LocalDate;

public record RevenueRequest(
        LocalDate date,
        String sector
) {
}
