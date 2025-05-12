package com.rkd.auto.service;

import com.rkd.auto.repository.RevenueRepository;
import com.rkd.auto.request.RevenueRequest;
import com.rkd.auto.response.RevenueResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class RevenueService {

    private static final Logger log = LoggerFactory.getLogger(RevenueService.class);

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
                ))
                .doOnNext(response ->
                        log.info("Receita encontrada: setor={}, data={}, valor={} {}",
                                request.sector(), request.date(),
                                response.amount(), response.currency()))
                .doOnError(error ->
                        log.error("Erro ao buscar receita para setor={} e data={}: {}",
                                request.sector(), request.date(), error.getMessage(), error));
    }
}
