package com.rkd.auto.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.rkd.auto.config.TestConfig;
import com.rkd.auto.model.SectorModel;
import com.rkd.auto.model.SpotModel;
import com.rkd.auto.model.VehicleModel;
import com.rkd.auto.repository.SectorRepository;
import com.rkd.auto.repository.SpotRepository;
import com.rkd.auto.repository.VehicleRepository;
import com.rkd.auto.type.EventType;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;

import static com.rkd.auto.definition.ApiDefinition.Spot.POST_SPOT_STATUS;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest
@Import(TestConfig.class)
@AutoConfigureWebTestClient
class SpotControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private SpotRepository spotRepository;

    @Autowired
    private SectorRepository sectorRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private R2dbcEntityTemplate r2dbcEntityTemplate;

    @AfterEach
    void cleanUp() {
        vehicleRepository.deleteAll().block();
        spotRepository.deleteAll().block();
        sectorRepository.deleteAll().block();
    }

    @Test
    void mustReturnSpotStatusSuccessfully() {

        double lat = -23.55052;
        double lng = -46.633308;
        String sectorName = "SECTOR_1";
        String licensePlate = "XYZ1234";
        ZonedDateTime now = ZonedDateTime.parse("2025-05-11T12:00:00Z");

        var sectorModel = new SectorModel(
                sectorName,
                10.0,
                100,
                LocalTime.of(8, 0),
                LocalTime.of(22, 0),
                240
        );

        r2dbcEntityTemplate.insert(SectorModel.class).using(sectorModel).block();

        SpotModel spotModel = new SpotModel(
                null,
                sectorName,
                lat,
                lng,
                true,
                licensePlate
        );

        spotRepository.save(spotModel).block();

        VehicleModel entry = new VehicleModel(
                null,
                licensePlate,
                EventType.ENTRY,
                now,
                lat,
                lng
        );

        VehicleModel parked = new VehicleModel(
                null,
                licensePlate,
                EventType.PARKED,
                now.plusMinutes(2),
                lat,
                lng
        );

        vehicleRepository.saveAll(java.util.List.of(entry, parked)).collectList().block();

        ObjectNode request = objectMapper.createObjectNode()
                .put("lat", lat)
                .put("lng", lng);

        webTestClient.post()
                .uri(POST_SPOT_STATUS)
                .contentType(APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.ocupied").isEqualTo(true)
                .jsonPath("$.license_plate").isEqualTo(licensePlate)
                .jsonPath("$.entry_time").value(val -> {
                    ZonedDateTime responseTime = ZonedDateTime.parse(val.toString());
                    Assertions.assertEquals(entry.timestamp().toInstant(), responseTime.toInstant());
                })
                .jsonPath("$.parked_time").value(val -> {
                    ZonedDateTime responseTime = ZonedDateTime.parse(val.toString());
                    Assertions.assertEquals(parked.timestamp().toInstant(), responseTime.toInstant());
                });

        Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            SpotModel savedSpot = spotRepository.findByLatAndLng(lat, lng).block();
            Assertions.assertNotNull(savedSpot);
            Assertions.assertTrue(savedSpot.occupied());
        });
    }

    @Test
    void mustReturn400WhenLatIsNull() {
        ObjectNode request = objectMapper.createObjectNode()
                .putNull("lat")
                .put("lng", -46.633308);

        webTestClient.post()
                .uri(POST_SPOT_STATUS)
                .contentType(APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo("INVALID_INPUT")
                .jsonPath("$.message").isEqualTo("The 'lat' field cannot be null");
    }

    @Test
    void mustReturn400WhenLngIsNull() {
        ObjectNode request = objectMapper.createObjectNode()
                .put("lat", -23.55052)
                .putNull("lng");

        webTestClient.post()
                .uri(POST_SPOT_STATUS)
                .contentType(APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo("INVALID_INPUT")
                .jsonPath("$.message").isEqualTo("The 'lng' field cannot be null");
    }
}
