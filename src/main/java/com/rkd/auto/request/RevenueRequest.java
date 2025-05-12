package com.rkd.auto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

import static com.rkd.auto.definition.MessageDefinition.Revenue.DATE_CANNOT_NULL;
import static com.rkd.auto.definition.MessageDefinition.Revenue.SECTOR_CANNOT_BLANK;

@Schema(description = "Payload containing the parameters to request revenue calculation.")
public record RevenueRequest(

        @Schema(description = "Date for which revenue should be calculated", example = "2025-01-01")
        @NotNull(message = DATE_CANNOT_NULL)
        LocalDate date,

        @Schema(description = "Sector name (e.g., A, B)", example = "A")
        @NotNull(message = SECTOR_CANNOT_BLANK)
        String sector

) {}
