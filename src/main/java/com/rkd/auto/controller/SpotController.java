package com.rkd.auto.controller;

import com.rkd.auto.request.SpotStatusRequest;
import com.rkd.auto.response.SpotStatusResponse;
import com.rkd.auto.service.SpotService;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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

    @PostMapping
    public Mono<SpotStatusResponse> getSpotStatus(@RequestBody @Valid SpotStatusRequest spotStatusRequest) {
        return spotService.getSpotStatus(spotStatusRequest);
    }
}
