package com.rkd.auto.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("spot")
public record SpotModel(
        @Id
        Long id,
        String sector,
        double lat,
        double lng,
        boolean occupied,
        String licensePlate
) {
}