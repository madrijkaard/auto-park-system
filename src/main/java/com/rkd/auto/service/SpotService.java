package com.rkd.auto.service;

import com.rkd.auto.model.SpotModel;
import com.rkd.auto.model.VehicleModel;
import com.rkd.auto.repository.SpotRepository;
import com.rkd.auto.repository.VehicleRepository;
import com.rkd.auto.request.SpotStatusRequest;
import com.rkd.auto.response.SpotStatusResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class SpotService {

    private static final Logger log = LoggerFactory.getLogger(SpotService.class);

    private final SpotRepository spotRepository;
    private final VehicleRepository vehicleRepository;

    public SpotService(SpotRepository spotRepository, VehicleRepository vehicleRepository) {
        this.spotRepository = spotRepository;
        this.vehicleRepository = vehicleRepository;
    }

    public Mono<SpotStatusResponse> getSpotStatus(SpotStatusRequest request) {
        return findSpotByCoordinates(request)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Vaga não encontrada para coordenadas informadas")))
                .flatMap(this::buildSpotStatusResponse)
                .doOnSuccess(response ->
                        log.info("Status da vaga consultado: placa={}, ocupada={}", response.licensePlate(), response.ocupied()))
                .doOnError(error ->
                        log.error("Erro ao obter status da vaga: {}", error.getMessage(), error));
    }

    private Mono<SpotModel> findSpotByCoordinates(SpotStatusRequest request) {
        return spotRepository.findByLatAndLng(request.lat(), request.lng());
    }

    private Mono<SpotStatusResponse> buildSpotStatusResponse(SpotModel spot) {
        String licensePlate = spot.licensePlate() != null ? spot.licensePlate() : "";

        return vehicleRepository.findByLicensePlateOrderByTimestampDesc(licensePlate)
                .collectList()
                .map(vehicleEvents -> toSpotStatusResponse(spot, vehicleEvents));
    }

    private SpotStatusResponse toSpotStatusResponse(SpotModel spot, List<VehicleModel> vehicleEvents) {
        VehicleModel entryEvent = findEventByType(vehicleEvents, "ENTRY");
        VehicleModel parkedEvent = findEventByType(vehicleEvents, "PARKED");

        double price = 0.0;

        return new SpotStatusResponse(
                spot.occupied(),
                spot.licensePlate(),
                price,
                entryEvent != null ? entryEvent.timestamp() : null,
                parkedEvent != null ? parkedEvent.timestamp() : null
        );
    }

    private VehicleModel findEventByType(List<VehicleModel> events, String eventType) {
        return events.stream()
                .filter(e -> eventType.equalsIgnoreCase(e.eventType().name()))
                .findFirst()
                .orElse(null);
    }
}
