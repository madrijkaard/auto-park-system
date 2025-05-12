package com.rkd.auto.controller;

import com.rkd.auto.request.RevenueRequest;
import com.rkd.auto.response.RevenueResponse;
import com.rkd.auto.service.RevenueService;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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

    @GetMapping
    public Mono<RevenueResponse> getRevenue(@ModelAttribute @Valid RevenueRequest revenueRequest) {
        return revenueService.getRevenue(revenueRequest);
    }
}
