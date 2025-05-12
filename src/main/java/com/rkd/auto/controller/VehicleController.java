package com.rkd.auto.controller;

import com.rkd.auto.producer.VehicleProducer;
import com.rkd.auto.request.VehicleRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/webhook")
public class VehicleController {

    private final VehicleProducer vehicleProducer;

    public VehicleController(VehicleProducer vehicleProducer) {
        this.vehicleProducer = vehicleProducer;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<Void> sendEvent(@RequestBody VehicleRequest vehicleRequest) {

        com.rkd.auto.grpc.VehicleRequest.Builder builder = com.rkd.auto.grpc.VehicleRequest.newBuilder()
                .setLicensePlate(vehicleRequest.licensePlate())
                .setEventType(vehicleRequest.eventType());

        if (vehicleRequest.entryTime() != null) {
            builder.setEntryTime(vehicleRequest.entryTime().toString());
        }

        if (vehicleRequest.exitTime() != null) {
            builder.setExitTime(vehicleRequest.exitTime().toString());
        }

        if (vehicleRequest.lat() != null) {
            builder.setLat(vehicleRequest.lat());
        }

        if (vehicleRequest.lng() != null) {
            builder.setLng(vehicleRequest.lng());
        }

        return vehicleProducer.sendEvent(builder.build());
    }
}
