package com.rkd.auto.controller;

import com.rkd.auto.request.PlateStatusRequest;
import com.rkd.auto.response.PlateStatusResponse;
import com.rkd.auto.service.PlateService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/plate-status")
public class PlateController {

    private final PlateService plateService;

    public PlateController(PlateService plateService) {
        this.plateService = plateService;
    }

    @PostMapping
    public Mono<PlateStatusResponse> getPlateStatus(@RequestBody PlateStatusRequest request) {
        return plateService.getPlateStatus(request)
                .onErrorMap(IllegalStateException.class, ex ->
                        new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage(), ex));
    }
}
