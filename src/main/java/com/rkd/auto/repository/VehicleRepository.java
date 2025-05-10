package com.rkd.auto.repository;

import com.rkd.auto.model.VehicleModel;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface VehicleRepository extends ReactiveCrudRepository<VehicleModel, Long> {
    Flux<VehicleModel> findByLicensePlateOrderByTimestampDesc(String licensePlate);
}
