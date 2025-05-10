package com.rkd.auto.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.rkd.auto.config.TestConfig;
import com.rkd.auto.model.VehicleModel;
import com.rkd.auto.repository.VehicleRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Import(TestConfig.class)
@AutoConfigureWebTestClient
public class VehicleControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private VehicleRepository vehicleRepository;

    @AfterEach
    void tearDown() {
        vehicleRepository.deleteAll().block();
    }

    @Test
    void mustRegisterLicensePlateSuccessfully() {

        ObjectNode objectNode = objectMapper.createObjectNode()
                .put("license_plate", "ABC9999")
                .put("event_type", "ENTRY")
                .put("entry_time", "2025-05-09T23:10:00Z")
                .put("exit_time", "")
                .put("lat", -23.55052)
                .put("lng", -46.633308);

        webTestClient.post()
                .uri("/webhook")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectNode)
                .exchange()
                .expectStatus().isAccepted();

        VehicleModel vehicleModel = vehicleRepository
                .findByLicensePlateOrderByTimestampDesc("ABC9999")
                .blockFirst();

        assertNotNull(vehicleModel);
        assertEquals("ABC9999", vehicleModel.licensePlate());
    }
}

