package com.rkd.auto.service;

import com.rkd.auto.model.VehicleModel;
import com.rkd.auto.repository.SpotRepository;
import com.rkd.auto.repository.VehicleRepository;
import com.rkd.auto.request.PlateStatusRequest;
import com.rkd.auto.response.PlateStatusResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

@Service
public class PlateService {

    private static final Logger log = LoggerFactory.getLogger(PlateService.class);

    private final VehicleRepository vehicleRepository;
    private final SpotRepository spotRepository;
    private final PricingCacheService pricingCacheService;

    public PlateService(
            VehicleRepository vehicleRepository,
            SpotRepository spotRepository,
            PricingCacheService pricingCacheService
    ) {
        this.vehicleRepository = vehicleRepository;
        this.spotRepository = spotRepository;
        this.pricingCacheService = pricingCacheService;
    }

    public Mono<PlateStatusResponse> getPlateStatus(PlateStatusRequest request) {
        String plate = request.licensePlate();

        return vehicleRepository.findByLicensePlateOrderByTimestampDesc(plate)
                .collectList()
                .flatMap(events -> {
                    VehicleModel entry = findEvent(events, "ENTRY");
                    VehicleModel parked = findEvent(events, "PARKED");

                    if (parked != null && parked.isValidParked() && entry != null) {
                        return buildParkedResponse(plate, entry, parked);
                    }

                    PlateStatusResponse response = buildNotParkedResponse(plate, entry);

                    log.info("Placa {} não está estacionada. Última entrada: {}", plate, response.entryTime());

                    return Mono.just(response);
                })
                .doOnSuccess(response ->
                        log.info("Status da placa '{}' resolvido com sucesso: {}", plate, response))
                .doOnError(error ->
                        log.error("Erro ao processar status da placa '{}': {}", plate, error.getMessage(), error));
    }

    private Mono<PlateStatusResponse> buildParkedResponse(String plate, VehicleModel entry, VehicleModel parked) {
        return spotRepository.findByLatAndLng(parked.lat(), parked.lng())
                .flatMap(spot ->
                        pricingCacheService.getPriceForSector(spot.sector())
                                .map(BigDecimal::doubleValue)
                                .map(pricePerHour -> {
                                    if (pricePerHour == Double.MAX_VALUE) {
                                        throw new IllegalStateException("Setor está totalmente lotado. Entrada não permitida.");
                                    }

                                    long minutes = Duration.between(entry.timestamp(), parked.timestamp()).toMinutes();
                                    double totalPrice = roundToTwoDecimals(pricePerHour * (minutes / 60.0));

                                    PlateStatusResponse response = new PlateStatusResponse(
                                            plate,
                                            totalPrice,
                                            entry.timestamp(),
                                            parked.timestamp(),
                                            parked.lat(),
                                            parked.lng()
                                    );

                                    log.info("Placa {} está estacionada em setor {}. Preço/hora: {}, Total: {}",
                                            plate, spot.sector(), pricePerHour, totalPrice);

                                    return response;
                                })
                );
    }

    private PlateStatusResponse buildNotParkedResponse(String plate, VehicleModel entry) {
        return new PlateStatusResponse(
                plate,
                0.0,
                entry != null ? entry.timestamp() : null,
                null,
                null,
                null
        );
    }

    private VehicleModel findEvent(List<VehicleModel> events, String type) {
        return events.stream()
                .filter(e -> type.equalsIgnoreCase(e.eventType().name()))
                .findFirst()
                .orElse(null);
    }

    private double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
