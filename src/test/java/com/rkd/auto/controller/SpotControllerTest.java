package com.rkd.auto.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.rkd.auto.config.TestConfig;
import com.rkd.auto.repository.SectorRepository;
import com.rkd.auto.repository.SpotRepository;
import com.rkd.auto.repository.VehicleRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.test.web.reactive.server.WebTestClient;

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

    @Test
    void mustReturn404WhenSpotIsNotFound() {

        double lat = -99.9999;
        double lng = -99.9999;

        ObjectNode request = objectMapper.createObjectNode()
                .put("lat", lat)
                .put("lng", lng);

        webTestClient.post()
                .uri(POST_SPOT_STATUS)
                .contentType(APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.code").isEqualTo("NOT_FOUND")
                .jsonPath("$.message").isEqualTo("Spot not found");
    }
}
