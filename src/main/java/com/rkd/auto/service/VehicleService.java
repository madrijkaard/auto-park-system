package com.rkd.auto.service;

import com.rkd.auto.model.SpotModel;
import com.rkd.auto.model.VehicleModel;
import com.rkd.auto.repository.SpotRepository;
import com.rkd.auto.repository.VehicleRepository;
import com.rkd.auto.request.VehicleRequest;
import com.rkd.auto.type.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;

@Service
public class VehicleService {

    private static final Logger log = LoggerFactory.getLogger(VehicleService.class);

    private final VehicleRepository vehicleRepository;
    private final SpotRepository spotRepository;

    public VehicleService(VehicleRepository vehicleRepository, SpotRepository spotRepository) {
        this.vehicleRepository = vehicleRepository;
        this.spotRepository = spotRepository;
    }

    public Mono<VehicleModel> registerParkingEvent(VehicleRequest vehicleRequest) {

        validateTimestamps(vehicleRequest);

        ZonedDateTime timestamp =
                vehicleRequest.entryTime() != null ? vehicleRequest.entryTime()
                        : vehicleRequest.exitTime() != null ? vehicleRequest.exitTime()
                        : ZonedDateTime.now();

        VehicleModel event = VehicleModel.of(
                vehicleRequest.licensePlate(),
                vehicleRequest.eventType(),
                timestamp,
                vehicleRequest.lat(),
                vehicleRequest.lng()
        );

        return handleSpotUpdate(vehicleRequest)
                .then(vehicleRepository.save(event))
                .doOnNext(saved -> log.info("Veículo salvo com sucesso: placa={}, timestamp={}", saved.licensePlate(), saved.timestamp()))
                .doOnError(error -> log.error("Erro ao salvar veículo: {}", error.getMessage(), error));
    }

    private void validateTimestamps(VehicleRequest request) {

        ZonedDateTime entryTime = request.entryTime();
        ZonedDateTime exitTime = request.exitTime();
        EventType type = EventType.valueOf(request.eventType().toUpperCase());

        switch (type) {
            case ENTRY -> {
                if (entryTime == null)
                    throw new IllegalArgumentException("entry_time deve estar preenchido para ENTRY.");
                if (exitTime != null)
                    throw new IllegalArgumentException("exit_time não deve estar preenchido para ENTRY.");
            }
            case PARKED -> {
                boolean oneFilled = (entryTime == null) ^ (exitTime == null);
                if (!oneFilled)
                    throw new IllegalArgumentException("Para PARKED, preencha somente entry_time OU exit_time.");
            }
            case EXIT -> {
                if (exitTime == null)
                    throw new IllegalArgumentException("exit_time deve estar preenchido para EXIT.");
                if (entryTime != null)
                    throw new IllegalArgumentException("entry_time não deve estar preenchido para EXIT.");
            }
        }
    }

    private Mono<Void> handleSpotUpdate(VehicleRequest req) {

        EventType type = EventType.valueOf(req.eventType().toUpperCase());

        if (type != EventType.PARKED) {
            return Mono.empty();
        }

        return spotRepository.findByLatAndLng(req.lat(), req.lng())
                .flatMap(spot -> {
                    boolean arriving = req.entryTime() != null;
                    SpotModel updatedSpot = new SpotModel(
                            spot.id(),
                            spot.sector(),
                            spot.lat(),
                            spot.lng(),
                            arriving,
                            arriving ? req.licensePlate() : ""
                    );
                    return spotRepository.save(updatedSpot).then();
                })
                .switchIfEmpty(Mono.fromRunnable(() -> log.warn("Nenhuma vaga encontrada para lat={}, lng={}", req.lat(), req.lng())));
    }
}
