package com.rkd.auto.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.ZonedDateTime;

@Table("revenue")
public record RevenueModel(
        @Id
        Long id,
        String sector,
        LocalDate date,
        double amount,
        String currency,
        ZonedDateTime timestamp
) {
}