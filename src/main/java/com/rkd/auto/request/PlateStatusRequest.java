package com.rkd.auto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PlateStatusRequest(
        @JsonProperty("license_plate") String licensePlate
) {
}