package com.rkd.auto.aspect;

import com.rkd.auto.cassandra.PriceCassandra;
import com.rkd.auto.model.SpotModel;
import com.rkd.auto.model.VehicleModel;
import com.rkd.auto.repository.PriceCassandraRepository;
import com.rkd.auto.repository.SpotRepository;
import com.rkd.auto.service.PricingService;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Aspect
@Component
public class PricingAspect {

    private static final Logger log = LoggerFactory.getLogger(PricingAspect.class);

    private final PricingService pricingService;
    private final SpotRepository spotRepository;
    private final PriceCassandraRepository cacheRepo;

    public PricingAspect(PricingService pricingService,
                         SpotRepository spotRepository,
                         PriceCassandraRepository cacheRepo) {
        this.pricingService = pricingService;
        this.spotRepository = spotRepository;
        this.cacheRepo = cacheRepo;
    }

    /**
     * Executa depois que VehicleService.registerParkingEvent(..) conclui
     */
    @AfterReturning(
            pointcut = "execution(* com.rkd.auto.service.VehicleService.registerParkingEvent(..))",
            returning = "result")
    public void updatePriceAfterEvent(Mono<?> result) {

        result.subscribe(event -> {
            if (event instanceof VehicleModel vehicle) {

                double lat = vehicle.lat();
                double lng = vehicle.lng();

                spotRepository.findByLatAndLng(lat, lng)
                        .flatMap(this::updatePriceForSector)       // grava/atualiza
                        .switchIfEmpty(Mono.fromRunnable(() ->
                                log.warn("Nenhuma vaga encontrada para lat={}, lng={}", lat, lng)))
                        .subscribe();
            }
        });
    }

    /**
     * Recalcula e faz UPSERT do preço para o setor informado
     */
    private Mono<Boolean> updatePriceForSector(SpotModel spot) {

        String sector = spot.sector();

        return pricingService.calculateCurrentPriceBySector(sector)
                .map(price -> new PriceCassandra(sector, price, Instant.now()))
                .flatMap(cacheRepo::save)
                .doOnSuccess(e ->
                        log.info("Preço salvo/atualizado no Scylla para setor '{}'", sector))
                .doOnError(e ->
                        log.error("Falha ao salvar preço p/ setor '{}': {}", sector, e.getMessage()))
                .thenReturn(Boolean.TRUE);
    }
}
