package com.rkd.auto.controller;

import com.rkd.auto.request.RevenueRequest;
import com.rkd.auto.response.RevenueResponse;
import com.rkd.auto.service.RevenueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static com.rkd.auto.definition.ApiDefinition.Revenue.GET_REVENUE;

@Validated
@RestController
@RequestMapping(GET_REVENUE)
public class RevenueController {

    private final RevenueService revenueService;

    public RevenueController(RevenueService revenueService) {
        this.revenueService = revenueService;
    }

    @Operation(
            summary = "Retrieve total revenue for a sector",
            description = "Calculates and returns the revenue collected for a specific sector on a given date."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Revenue calculated successfully",
                    content = @Content(schema = @Schema(implementation = RevenueResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input parameters", content = @Content)
    })
    @GetMapping
    public Mono<RevenueResponse> getRevenue(
            @Parameter(description = "Revenue request with date and sector name")
            @ModelAttribute @Valid RevenueRequest revenueRequest
    ) {
        return revenueService.getRevenue(revenueRequest);
    }
}
