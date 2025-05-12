package com.rkd.auto.service;

import com.rkd.auto.model.SectorModel;
import com.rkd.auto.model.SpotModel;
import com.rkd.auto.repository.SectorRepository;
import com.rkd.auto.repository.SpotRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class PricingService {

    private static final Logger log = LoggerFactory.getLogger(PricingService.class);

    private final SpotRepository spotRepository;
    private final SectorRepository sectorRepository;

    public PricingService(SpotRepository spotRepository, SectorRepository sectorRepository) {
        this.spotRepository = spotRepository;
        this.sectorRepository = sectorRepository;
    }

    public Mono<Double> calculateCurrentPriceBySector(String sectorName) {
        return sectorRepository.findByName(sectorName)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Setor não encontrado: " + sectorName)))
                .flatMap(sector -> findSpotsBySector(sectorName)
                        .collectList()
                        .map(spots -> {
                            double price = calculateDynamicPrice(sector, spots);
                            log.info("Preço calculado para setor '{}': {} ({} vagas, {} ocupadas)",
                                    sectorName, price, spots.size(),
                                    spots.stream().filter(SpotModel::occupied).count());
                            return price;
                        }))
                .doOnError(error ->
                        log.error("Erro ao calcular preço para setor '{}': {}", sectorName, error.getMessage(), error));
    }

    private Flux<SpotModel> findSpotsBySector(String sectorName) {
        return spotRepository.findAll()
                .filter(spot -> sectorName.equalsIgnoreCase(spot.sector()));
    }

    private double calculateDynamicPrice(SectorModel sector, List<SpotModel> spots) {
        int totalSpots = spots.size();
        long occupiedCount = spots.stream().filter(SpotModel::occupied).count();
        double occupancyRate = calculateOccupancyRate(occupiedCount, totalSpots);

        if (occupancyRate >= 1.0) {
            throw new IllegalStateException("Setor '" + sector.name() + "' está 100% ocupado. Entrada não permitida.");
        }

        double basePrice = sector.basePrice();
        double adjustedPrice = adjustPriceByOccupancy(basePrice, occupancyRate);
        return roundToTwoDecimals(adjustedPrice);
    }


    private double calculateOccupancyRate(long occupied, int total) {
        return total > 0 ? (double) occupied / total : 0.0;
    }

    private double adjustPriceByOccupancy(double basePrice, double rate) {
        if (rate <= 0.25) return basePrice * 0.90;
        else if (rate <= 0.50) return basePrice;
        else if (rate <= 0.75) return basePrice * 1.10;
        else if (rate < 1.00) return basePrice * 1.25;
        else return Double.MAX_VALUE;
    }

    private double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
