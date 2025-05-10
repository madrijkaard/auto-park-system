package com.rkd.auto.model;

import com.rkd.auto.type.EventType;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.ZonedDateTime;

@Table("vehicle")
public record VehicleModel(
        @Id
        Long id,
        String licensePlate,
        EventType eventType,
        ZonedDateTime timestamp,
        Double lat,
        Double lng
) {
    public static VehicleModel of(
            String licensePlate,
            String eventTypeStr,
            ZonedDateTime timestamp,
            Double lat,
            Double lng
    ) {
        return new VehicleModel(
                null,
                licensePlate,
                EventType.valueOf(eventTypeStr.toUpperCase()),
                timestamp,
                lat,
                lng
        );
    }

    public boolean isValidParked() {
        return this.lat != null && this.lng != null;
    }
}