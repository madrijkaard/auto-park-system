package com.rkd.auto.service;

import com.rkd.auto.repository.RevenueRepository;
import com.rkd.auto.request.RevenueRequest;
import com.rkd.auto.response.RevenueResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class RevenueService {

    private final RevenueRepository revenueRepository;

    public RevenueService(RevenueRepository revenueRepository) {
        this.revenueRepository = revenueRepository;
    }

    public Mono<RevenueResponse> getRevenue(RevenueRequest request) {
        return revenueRepository.findBySectorAndDate(request.sector(), request.date())
                .map(revenue -> new RevenueResponse(
                        revenue.amount(),
                        revenue.currency(),
                        revenue.timestamp()
                ));
    }
}
