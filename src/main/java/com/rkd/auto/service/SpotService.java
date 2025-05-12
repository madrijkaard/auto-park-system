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
    private final PricingService pricingService;

    public SpotService(
            SpotRepository spotRepository,
            VehicleRepository vehicleRepository,
            PricingService pricingService
    ) {
        this.spotRepository = spotRepository;
        this.vehicleRepository = vehicleRepository;
        this.pricingService = pricingService;
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
                .flatMap(vehicleEvents -> {
                    VehicleModel entryEvent = findEventByType(vehicleEvents, "ENTRY");
                    VehicleModel parkedEvent = findEventByType(vehicleEvents, "PARKED");

                    if (parkedEvent == null) {
                        return Mono.just(new SpotStatusResponse(
                                spot.occupied(),
                                spot.licensePlate(),
                                0.0,
                                entryEvent != null ? entryEvent.timestamp() : null,
                                null
                        ));
                    }

                    return pricingService.calculateCurrentPriceBySector(spot.sector())
                            .onErrorReturn(Double.MAX_VALUE)
                            .map(pricePerHour -> {
                                long minutesParked = java.time.Duration.between(parkedEvent.timestamp(), java.time.ZonedDateTime.now()).toMinutes();
                                double hours = minutesParked / 60.0;
                                double totalPrice = roundToTwoDecimals(pricePerHour * hours);

                                return new SpotStatusResponse(
                                        spot.occupied(),
                                        spot.licensePlate(),
                                        totalPrice,
                                        entryEvent != null ? entryEvent.timestamp() : null,
                                        parkedEvent.timestamp()
                                );
                            });
                });
    }

    private double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private VehicleModel findEventByType(List<VehicleModel> events, String eventType) {
        return events.stream()
                .filter(e -> eventType.equalsIgnoreCase(e.eventType().name()))
                .findFirst()
                .orElse(null);
    }
}
