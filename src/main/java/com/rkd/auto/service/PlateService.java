package com.rkd.auto.service;

import com.rkd.auto.model.VehicleModel;
import com.rkd.auto.repository.SpotRepository;
import com.rkd.auto.repository.VehicleRepository;
import com.rkd.auto.request.PlateStatusRequest;
import com.rkd.auto.response.PlateStatusResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class PlateService {

    private final VehicleRepository vehicleRepository;
    private final SpotRepository spotRepository;
    private final PricingService pricingService;

    public PlateService(
            VehicleRepository vehicleRepository,
            SpotRepository spotRepository,
            PricingService pricingService
    ) {
        this.vehicleRepository = vehicleRepository;
        this.spotRepository = spotRepository;
        this.pricingService = pricingService;
    }

    public Mono<PlateStatusResponse> getPlateStatus(PlateStatusRequest request) {
        return vehicleRepository.findByLicensePlateOrderByTimestampDesc(request.licensePlate())
                .collectList()
                .flatMap(events -> buildResponse(request.licensePlate(), events));
    }

    private Mono<PlateStatusResponse> buildResponse(String plate, List<VehicleModel> vehicles) {

        VehicleModel entry = findEvent(vehicles, "ENTRY");
        VehicleModel parked = findEvent(vehicles, "PARKED");

        if (parked != null && parked.isValidParked()) {
            return buildParkedResponse(plate, entry, parked);
        }

        return Mono.just(buildNotParkedResponse(plate, entry));
    }



    private Mono<PlateStatusResponse> buildParkedResponse(String plate, VehicleModel entry, VehicleModel parked) {
        return spotRepository.findByLatAndLng(parked.lat(), parked.lng())
                .flatMap(spot -> pricingService.calculateCurrentPriceBySector(spot.sector())
                        .map(preco -> new PlateStatusResponse(
                                plate,
                                preco,
                                entry != null ? entry.timestamp() : null,
                                parked.timestamp(),
                                parked.lat(),
                                parked.lng()
                        )));
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
}

