package com.rkd.auto.producer;

import com.rkd.auto.grpc.VehicleRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class VehicleProducer {

    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    public VehicleProducer(KafkaTemplate<String, byte[]> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public Mono<Void> sendEvent(VehicleRequest request) {
        return Mono.create(sink ->
                kafkaTemplate
                        .send("vehicle-events", request.toByteArray())
                        .whenComplete((result, ex) -> {
                            if (ex != null) {
                                sink.error(ex);
                            } else {
                                sink.success();
                            }
                        })
        );
    }
}
