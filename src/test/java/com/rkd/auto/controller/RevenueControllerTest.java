package com.rkd.auto.controller;

import com.rkd.auto.config.TestConfig;
import com.rkd.auto.model.RevenueModel;
import com.rkd.auto.repository.RevenueRepository;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static com.rkd.auto.definition.ApiDefinition.Revenue.GET_REVENUE;

@SpringBootTest
@Import(TestConfig.class)
@AutoConfigureWebTestClient
class RevenueControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private RevenueRepository revenueRepository;

    @AfterEach
    void cleanUp() {
        revenueRepository.deleteAll().block();
    }

    @Test
    void mustReturnRevenueSuccessfully() {

        String sector = "SECTOR_ABC";
        LocalDate date = LocalDate.of(2025, 5, 11);
        double amount = 12345.67;
        String currency = "BRL";
        ZonedDateTime timestamp = ZonedDateTime.parse("2025-05-11T10:00:00Z");

        RevenueModel model = new RevenueModel(
                null,
                sector,
                date,
                amount,
                currency,
                timestamp
        );
        revenueRepository.save(model).block();

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(GET_REVENUE)
                        .queryParam("sector", sector)
                        .queryParam("date", date.toString())
                        .build()
                )
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.amount").isEqualTo(amount)
                .jsonPath("$.currency").isEqualTo(currency)
                .jsonPath("$.timestamp").value(val -> {
                    ZonedDateTime responseTime = ZonedDateTime.parse(val.toString());
                    assertEquals(timestamp.toInstant(), responseTime.toInstant());
                });

        Awaitility.await().atMost(3, TimeUnit.SECONDS).untilAsserted(() -> {
            RevenueModel saved = revenueRepository.findBySectorAndDate(sector, date).block();
            assertNotNull(saved);
            assertEquals(amount, saved.amount());
        });
    }
}
