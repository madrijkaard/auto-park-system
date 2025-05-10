package com.rkd.auto.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalTime;

@Table("sector")
public record SectorModel(
        @Id
        String name,
        double basePrice,
        int maxCapacity,
        LocalTime openHour,
        LocalTime closeHour,
        int durationLimitMinutes
) {
}