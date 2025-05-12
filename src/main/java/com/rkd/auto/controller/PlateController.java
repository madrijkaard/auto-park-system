package com.rkd.auto.controller;

import com.rkd.auto.request.PlateStatusRequest;
import com.rkd.auto.response.PlateStatusResponse;
import com.rkd.auto.service.PlateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import static com.rkd.auto.definition.ApiDefinition.Plate.POST_PLATE_STATUS;
import static org.springframework.http.HttpStatus.CONFLICT;

@Validated
@RestController
@RequestMapping(POST_PLATE_STATUS)
public class PlateController {

    private final PlateService plateService;

    public PlateController(PlateService plateService) {
        this.plateService = plateService;
    }

    @Operation(
            summary = "Retrieve status of a vehicle by license plate",
            description = "Returns current parking status, price accumulated, location, and entry information for a specific vehicle."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plate status retrieved successfully",
                    content = @Content(schema = @Schema(implementation = PlateStatusResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid license plate provided", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflicting state (e.g., multiple active entries)", content = @Content)
    })
    @PostMapping
    public Mono<PlateStatusResponse> getPlateStatus(@RequestBody @Valid PlateStatusRequest plateStatusRequest) {
        return plateService.getPlateStatus(plateStatusRequest)
                .onErrorMap(IllegalStateException.class, ex ->
                        new ResponseStatusException(CONFLICT, ex.getMessage(), ex));
    }
}
