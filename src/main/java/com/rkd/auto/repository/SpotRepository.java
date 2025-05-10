package com.rkd.auto.repository;

import com.rkd.auto.model.SpotModel;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface SpotRepository extends ReactiveCrudRepository<SpotModel, Long> {
    Mono<SpotModel> findByLatAndLng(double lat, double lng);
}
