package com.rkd.auto.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.rkd.auto.config.TestConfig;
import com.rkd.auto.model.VehicleModel;
import com.rkd.auto.repository.VehicleRepository;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;

import static com.rkd.auto.definition.ApiDefinition.Vehicle.POST_WEBHOOK;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest
@Import(TestConfig.class)
@AutoConfigureWebTestClient
class VehicleControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    void cleanUp() {
        vehicleRepository.deleteAll().block();
    }

    @Test
    void mustRegisterLicensePlateSuccessfully() {

        ObjectNode objectNode = objectMapper.createObjectNode()
                .put("license_plate", "ABC9999")
                .put("event_type", "ENTRY")
                .put("entry_time", ZonedDateTime.now().toString())
                .put("exit_time", "")
                .put("lat", -23.55052)
                .put("lng", -46.633308);

        webTestClient.post()
                .uri(POST_WEBHOOK)
                .contentType(APPLICATION_JSON)
                .bodyValue(objectNode)
                .exchange()
                .expectStatus().isAccepted();

        Awaitility.await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {

            VehicleModel vehicleModel = vehicleRepository
                    .findByLicensePlateOrderByTimestampDesc("ABC9999")
                    .blockFirst();

            Assertions.assertNotNull(vehicleModel);
            Assertions.assertEquals("ABC9999", vehicleModel.licensePlate());
        });
    }
}
