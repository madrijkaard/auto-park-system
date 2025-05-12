package com.rkd.auto.controller;

import com.rkd.auto.request.RevenueRequest;
import com.rkd.auto.response.RevenueResponse;
import com.rkd.auto.service.RevenueService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/revenue")
public class RevenueController {

    private final RevenueService revenueService;

    public RevenueController(RevenueService revenueService) {
        this.revenueService = revenueService;
    }

    @GetMapping
    public Mono<RevenueResponse> getRevenue(@ModelAttribute RevenueRequest revenueRequest) {
        return revenueService.getRevenue(revenueRequest);
    }
}
