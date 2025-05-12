package com.rkd.auto.controller;

import com.rkd.auto.producer.VehicleProducer;
import com.rkd.auto.request.VehicleRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static com.rkd.auto.definition.ApiDefinition.Vehicle.POST_WEBHOOK;

@Validated
@RestController
@RequestMapping(POST_WEBHOOK)
public class VehicleController {

    private final VehicleProducer vehicleProducer;

    public VehicleController(VehicleProducer vehicleProducer) {
        this.vehicleProducer = vehicleProducer;
    }

    @Operation(
            summary = "Receives events related to vehicle movement",
            description = "Handles ENTRY, EXIT and PARKED events by sending them to the event stream"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Event accepted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input provided", content = @Content)
    })
    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<Void> sendEvent(@RequestBody @Valid VehicleRequest vehicleRequest) {

        var entryTime = vehicleRequest.entryTime();
        var exitTime = vehicleRequest.exitTime();

        com.rkd.auto.grpc.VehicleRequest.Builder request = com.rkd.auto.grpc.VehicleRequest.newBuilder()
                .setLicensePlate(vehicleRequest.licensePlate())
                .setEventType(vehicleRequest.eventType())
                .setLat(vehicleRequest.lat())
                .setLng(vehicleRequest.lng());

        if (entryTime == null && exitTime == null) {
            throw new IllegalArgumentException("Either entry_time or exit_time must be provided.");
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
