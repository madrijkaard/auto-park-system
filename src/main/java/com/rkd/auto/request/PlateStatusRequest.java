package com.rkd.auto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

import static com.rkd.auto.definition.MessageDefinition.Plate.LICENSE_PLATE_CANNOT_BLANK;

public record PlateStatusRequest(
        @NotBlank(message = LICENSE_PLATE_CANNOT_BLANK)
        @JsonProperty("license_plate") String licensePlate
) {
}