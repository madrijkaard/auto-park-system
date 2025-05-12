package com.rkd.auto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

import static com.rkd.auto.definition.MessageDefinition.Revenue.DATE_CANNOT_NULL;
import static com.rkd.auto.definition.MessageDefinition.Revenue.SECTOR_CANNOT_BLANK;

public record RevenueRequest(
        @NotNull(message = DATE_CANNOT_NULL)
        LocalDate date,
        @NotNull(message = SECTOR_CANNOT_BLANK)
        String sector
) {
}
