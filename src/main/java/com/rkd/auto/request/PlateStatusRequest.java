package com.rkd.auto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import static com.rkd.auto.definition.MessageDefinition.Plate.LICENSE_PLATE_CANNOT_BLANK;

@Schema(description = "Payload containing the license plate to retrieve vehicle status.")
public record PlateStatusRequest(

        @Schema(description = "License plate of the vehicle", example = "ZUL0001")
        @NotBlank(message = LICENSE_PLATE_CANNOT_BLANK)
        @JsonProperty("license_plate")
        String licensePlate

) {
}
