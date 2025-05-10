package com.rkd.auto.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.rkd.auto.config.TestConfig;
import com.rkd.auto.model.VehicleModel;
import com.rkd.auto.repository.SpotRepository;
import com.rkd.auto.repository.VehicleRepository;
import com.rkd.auto.service.PricingService;
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
public class PlateControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private PricingService pricingService;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private SpotRepository spotRepository;

    @AfterEach
    void tearDown() {
        vehicleRepository.deleteAll().block();
        spotRepository.deleteAll().block();
    }

    @Test
    void mustRegisterLicensePlateSuccessfully() {

    }
}
