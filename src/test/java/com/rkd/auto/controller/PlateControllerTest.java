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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.IntStream;

import static com.rkd.auto.definition.ApiDefinition.Plate.POST_PLATE_STATUS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest
@Import(TestConfig.class)
@AutoConfigureWebTestClient
class PlateControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private SpotRepository spotRepository;

    @Autowired
    private SectorRepository sectorRepository;

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
    void mustReturn90PercentPriceWhenOccupancyIsBelowOrEqual25Percent() {
        runPricingTest(4, 1, 10.0 * 0.90);
    }

    @Test
    void mustReturnBasePriceWhenOccupancyIsBelowOrEqual50Percent() {
        runPricingTest(4, 2, 10.0);
    }

    @Test
    void mustReturn110PercentPriceWhenOccupancyIsBelowOrEqual75Percent() {
        runPricingTest(4, 3, 10.0 * 1.10);
    }

    @Test
    void mustReturn125PercentPriceWhenOccupancyIsBelow100Percent() {
        runPricingTest(5, 4, 10.0 * 1.25);
    }

    @Test
    void mustReturnConflictWhenAllSpotsAreOccupied() {
        runConflictTest(4, 4);
    }

    private void runPricingTest(int totalSpots, int occupiedCount, double expectedHourlyPrice) {

        String licensePlate = "TEST123";
        String sector = "DYNAMIC_SECTOR" + totalSpots + "_" + occupiedCount;
        double basePrice = 10.0;
        ZonedDateTime entryTime = ZonedDateTime.parse("2025-05-11T12:00:00Z");
        ZonedDateTime parkedTime = entryTime.plusMinutes(2);
        double lat = -23.5;
        double lng = -46.6;

        vehicleRepository.deleteAll().block();
        spotRepository.deleteAll().block();
        sectorRepository.deleteAll().block();

        SectorModel sectorModel = new SectorModel(
                sector,
                basePrice,
                totalSpots,
                LocalTime.of(6, 0),
                LocalTime.of(22, 0),
                120
        );

        r2dbcEntityTemplate.insert(SectorModel.class).using(sectorModel).block();

        for (int i = 0; i < totalSpots; i++) {
            boolean isOccupied = i < occupiedCount;
            String plate = isOccupied ? licensePlate : null;

            SpotModel spot = new SpotModel(
                    null,
                    sector,
                    lat + i * 0.0001,
                    lng + i * 0.0001,
                    isOccupied,
                    plate
            );

            spotRepository.save(spot).block();
        }

        if (occupiedCount > 0) {
            VehicleModel entry = new VehicleModel(
                    null,
                    licensePlate,
                    EventType.ENTRY,
                    entryTime,
                    lat,
                    lng
            );
            VehicleModel parked = new VehicleModel(
                    null,
                    licensePlate,
                    EventType.PARKED,
                    parkedTime,
                    lat,
                    lng
            );

            vehicleRepository.saveAll(List.of(entry, parked)).collectList().block();
        }

        ObjectNode request = objectMapper.createObjectNode()
                .put("license_plate", licensePlate);

        webTestClient.post()
                .uri(POST_PLATE_STATUS)
                .contentType(APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.price_until_now").value(val -> {
                    double actual = Double.parseDouble(val.toString());
                    double expectedTotal = expectedHourlyPrice * (2.0 / 60.0);
                    assertEquals(expectedTotal, actual, 0.01);
                });
    }

    private void runConflictTest(int totalSpots, int occupiedSpots) {

        String licensePlate = "TEST123";
        double lat = -23.5;
        double lng = -46.6;
        String sector = "DYNAMIC_SECTOR" + totalSpots + "_" + occupiedSpots;
        ZonedDateTime entryTime = ZonedDateTime.parse("2025-05-11T12:00:00Z");
        ZonedDateTime parkedTime = entryTime.plusMinutes(2);

        SectorModel sectorModel = new SectorModel(
                sector,
                10.0,
                totalSpots,
                LocalTime.of(6, 0),
                LocalTime.of(22, 0),
                120
        );

        r2dbcEntityTemplate.insert(SectorModel.class).using(sectorModel).block();

        List<SpotModel> spots = IntStream.range(0, totalSpots)
                .mapToObj(i -> new SpotModel(
                        null,
                        sector,
                        lat + i * 0.0001,
                        lng + i * 0.0001,
                        i < occupiedSpots,
                        i == 0 ? licensePlate : null
                ))
                .toList();

        spotRepository.saveAll(spots).collectList().block();

        VehicleModel entry = new VehicleModel(
                null, licensePlate, EventType.ENTRY, entryTime, lat, lng);
        VehicleModel parked = new VehicleModel(
                null, licensePlate, EventType.PARKED, parkedTime, lat, lng);

        vehicleRepository.saveAll(List.of(entry, parked)).collectList().block();

        ObjectNode request = objectMapper.createObjectNode()
                .put("license_plate", licensePlate);

        webTestClient.post()
                .uri(POST_PLATE_STATUS)
                .contentType(APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void mustReturn400WhenLicensePlateIsBlank() {
        ObjectNode request = objectMapper.createObjectNode()
                .put("license_plate", "");

        webTestClient.post()
                .uri(POST_PLATE_STATUS)
                .contentType(APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo("INVALID_INPUT")
                .jsonPath("$.message").isEqualTo("The 'license_plate' field cannot be blank");
    }
}
