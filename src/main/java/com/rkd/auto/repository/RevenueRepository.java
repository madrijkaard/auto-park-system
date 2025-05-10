package com.rkd.auto.repository;

import com.rkd.auto.model.RevenueModel;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface RevenueRepository extends ReactiveCrudRepository<RevenueModel, Long> {
    Mono<RevenueModel> findBySectorAndDate(String sector, LocalDate date);
}
