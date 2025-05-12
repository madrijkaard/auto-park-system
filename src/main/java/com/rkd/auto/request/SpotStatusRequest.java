package com.rkd.auto.request;

import jakarta.validation.constraints.NotNull;

import static com.rkd.auto.definition.MessageDefinition.Spot.LAT_CANNOT_NULL;
import static com.rkd.auto.definition.MessageDefinition.Spot.LNG_CANNOT_NULL;

public record SpotStatusRequest(
        @NotNull(message = LAT_CANNOT_NULL)
        Double lat,
        @NotNull(message = LNG_CANNOT_NULL)
        Double lng
) {
}
