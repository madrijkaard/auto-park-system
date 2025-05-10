package com.rkd.auto.service;

import com.rkd.auto.model.SpotModel;
import com.rkd.auto.model.VehicleModel;
import com.rkd.auto.repository.SpotRepository;
import com.rkd.auto.repository.VehicleRepository;
import com.rkd.auto.request.SpotStatusRequest;
import com.rkd.auto.response.SpotStatusResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class SpotService {

    private final SpotRepository spotRepository;
    private final VehicleRepository vehicleRepository;

    public SpotService(SpotRepository spotRepository, VehicleRepository vehicleRepository) {
        this.spotRepository = spotRepository;
        this.vehicleRepository = vehicleRepository;
    }

    public Mono<SpotStatusResponse> getSpotStatus(SpotStatusRequest request) {
        return findSpotByCoordinates(request)
                .flatMap(this::buildSpotStatusResponse);
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
                .filter(e -> eventType.equals(e.eventType().name()))
                .findFirst()
                .orElse(null);
    }
}
