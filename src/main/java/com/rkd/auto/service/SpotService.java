package com.rkd.auto.service;

import com.rkd.auto.exception.NotFoundException;
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

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

import static com.rkd.auto.type.ExceptionType.NOT_FOUND;

@Service
public class SpotService {

    private static final Logger log = LoggerFactory.getLogger(SpotService.class);

    private final SpotRepository spotRepository;
    private final VehicleRepository vehicleRepository;
    private final PricingCacheService pricingCacheService;

    public SpotService(
            SpotRepository spotRepository,
            VehicleRepository vehicleRepository,
            PricingCacheService pricingCacheService
    ) {
        this.spotRepository = spotRepository;
        this.vehicleRepository = vehicleRepository;
        this.pricingCacheService = pricingCacheService;
    }

    public Mono<SpotStatusResponse> getSpotStatus(SpotStatusRequest request) {
        return findSpotByCoordinates(request)
                .switchIfEmpty(Mono.error(new NotFoundException(NOT_FOUND, "Spot")))
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

        var licensePlate = spot.licensePlate() != null ? spot.licensePlate() : "";

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

                    return pricingCacheService.getPriceForSector(spot.sector())
                            .map(BigDecimal::doubleValue)
                            .map(pricePerHour -> {
                                long minutesParked = Duration.between(parkedEvent.timestamp(), java.time.ZonedDateTime.now()).toMinutes();
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