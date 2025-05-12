package com.rkd.auto.service;

import com.rkd.auto.model.VehicleModel;
import com.rkd.auto.repository.VehicleRepository;
import com.rkd.auto.request.VehicleRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;

@Service
public class VehicleService {

    private static final Logger log = LoggerFactory.getLogger(VehicleService.class);

    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public Mono<VehicleModel> processWebhookEvent(VehicleRequest vehicleRequest) {

        ZonedDateTime timestamp = vehicleRequest.entryTime() != null ? vehicleRequest.entryTime()
                : vehicleRequest.exitTime() != null ? vehicleRequest.exitTime()
                : ZonedDateTime.now();

        VehicleModel event = VehicleModel.of(
                vehicleRequest.licensePlate(),
                vehicleRequest.eventType(),
                timestamp,
                vehicleRequest.lat(),
                vehicleRequest.lng()
        );

        return vehicleRepository.save(event)
                .doOnNext(saved ->
                        log.info("Veículo salvo com sucesso: placa={}, timestamp={}",
                                saved.licensePlate(), saved.timestamp()))
                .doOnError(error ->
                        log.error("Erro ao salvar veículo: {}", error.getMessage(), error));
    }
}
