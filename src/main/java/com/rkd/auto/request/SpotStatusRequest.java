package com.rkd.auto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import static com.rkd.auto.definition.MessageDefinition.Spot.LAT_CANNOT_NULL;
import static com.rkd.auto.definition.MessageDefinition.Spot.LNG_CANNOT_NULL;

@Schema(description = "Payload to request the status of a specific parking spot.")
public record SpotStatusRequest(
        @Schema(description = "Latitude of the spot", example = "-23.561684")
        @NotNull(message = LAT_CANNOT_NULL)
        Double lat,

        @Schema(description = "Longitude of the spot", example = "-46.655981")
        @NotNull(message = LNG_CANNOT_NULL)
        Double lng
) {
}
