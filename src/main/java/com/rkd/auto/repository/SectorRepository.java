package com.rkd.auto.repository;

import com.rkd.auto.model.SectorModel;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface SectorRepository extends ReactiveCrudRepository<SectorModel, String> {
    Mono<SectorModel> findByName(String name);
}
