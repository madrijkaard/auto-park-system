package com.rkd.auto.service;

import com.rkd.auto.cassandra.PriceCassandra;
import com.rkd.auto.repository.PriceCassandraRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
public class PricingCacheService {

    private static final Logger log = LoggerFactory.getLogger(PricingCacheService.class);

    private final PricingService pricingService;
    private final PriceCassandraRepository cacheRepo;

    public PricingCacheService(PricingService pricingService,
                               PriceCassandraRepository cacheRepo) {
        this.pricingService = pricingService;
        this.cacheRepo = cacheRepo;
    }

    public Mono<BigDecimal> getPriceForSector(String sectorName) {

        return cacheRepo.findById(sectorName)
                .filter(this::isSameHour)
                .map(PriceCassandra::getPrice)
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("Preço no Scylla ausente ou desatualizado. "
                            + "Calculando preço para setor '{}'", sectorName);

                    return pricingService.calculateCurrentPriceBySector(sectorName)
                            .flatMap(price -> {
                                PriceCassandra entity =
                                        new PriceCassandra(sectorName, price, Instant.now());

                                return cacheRepo.save(entity)
                                        .thenReturn(price);
                            });
                }));
    }

    private boolean isSameHour(PriceCassandra entity) {

        ZonedDateTime cached = ZonedDateTime.ofInstant(
                entity.getUpdatedAt(), ZoneId.systemDefault());

        ZonedDateTime now = ZonedDateTime.now();

        return cached.toLocalDate().equals(now.toLocalDate()) &&
                cached.getHour() == now.getHour();
    }
}
