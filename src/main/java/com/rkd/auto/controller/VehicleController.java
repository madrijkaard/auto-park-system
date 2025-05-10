package com.rkd.auto.controller;

import com.rkd.auto.request.VehicleRequest;
import com.rkd.auto.service.VehicleService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/webhook")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<Void> handleEvent(@RequestBody VehicleRequest vehicleRequest) {
        return vehicleService.processWebhookEvent(vehicleRequest).then();
    }
}
