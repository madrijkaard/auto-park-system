package com.rkd.auto.controller;

import com.rkd.auto.request.SpotStatusRequest;
import com.rkd.auto.response.SpotStatusResponse;
import com.rkd.auto.service.SpotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static com.rkd.auto.definition.ApiDefinition.Spot.POST_SPOT_STATUS;

@Validated
@RestController
@RequestMapping(POST_SPOT_STATUS)
public class SpotController {

    private final SpotService spotService;

    public SpotController(SpotService spotService) {
        this.spotService = spotService;
    }

    @Operation(
            summary = "Check the status of a parking spot",
            description = "Verifies whether the specified parking spot is occupied and returns related data."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Spot status retrieved successfully",
                    content = @Content(schema = @Schema(implementation = SpotStatusResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content),
            @ApiResponse(responseCode = "404", description = "Spot not found", content = @Content)
    })
    @PostMapping
    public Mono<SpotStatusResponse> getSpotStatus(@RequestBody @Valid SpotStatusRequest spotStatusRequest) {
        return spotService.getSpotStatus(spotStatusRequest);
    }
}
