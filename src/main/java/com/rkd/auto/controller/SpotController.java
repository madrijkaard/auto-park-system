package com.rkd.auto.controller;

import com.rkd.auto.request.SpotStatusRequest;
import com.rkd.auto.response.SpotStatusResponse;
import com.rkd.auto.service.SpotService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/spot-status")
public class SpotController {

    private final SpotService spotService;

    public SpotController(SpotService spotService) {
        this.spotService = spotService;
    }

    @PostMapping
    public Mono<SpotStatusResponse> getSpotStatus(@RequestBody SpotStatusRequest request) {
        return spotService.getSpotStatus(request);
    }
}
