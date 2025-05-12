package com.rkd.auto.controller;

import com.rkd.auto.producer.VehicleProducer;
import com.rkd.auto.request.VehicleRequest;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static com.rkd.auto.definition.ApiDefinition.Vehicle.POST_WEBHOOK;
import static org.springframework.http.HttpStatus.ACCEPTED;

@Validated
@RestController
@RequestMapping(POST_WEBHOOK)
public class VehicleController {

    private final VehicleProducer vehicleProducer;

    public VehicleController(VehicleProducer vehicleProducer) {
        this.vehicleProducer = vehicleProducer;
    }

    @PostMapping
    @ResponseStatus(ACCEPTED)
    public Mono<Void> sendEvent(@RequestBody @Valid VehicleRequest vehicleRequest) {

        var entryTime = vehicleRequest.entryTime();
        var exitTime = vehicleRequest.exitTime();

        com.rkd.auto.grpc.VehicleRequest.Builder request = com.rkd.auto.grpc.VehicleRequest.newBuilder()
                .setLicensePlate(vehicleRequest.licensePlate())
                .setEventType(vehicleRequest.eventType())
                .setLat(vehicleRequest.lat())
                .setLng(vehicleRequest.lng());

        if (entryTime == null && exitTime == null) {
            throw new IllegalArgumentException();
        }

        if (entryTime != null) {
            request.setEntryTime(entryTime.toString());
        }

        if (exitTime != null) {
            request.setExitTime(exitTime.toString());
        }

        return vehicleProducer.sendEvent(request.build());
    }
}
