package com.rkd.auto.consumer;

import com.google.protobuf.InvalidProtocolBufferException;
import com.rkd.auto.grpc.WebhookEventRequest;
import com.rkd.auto.request.VehicleRequest;
import com.rkd.auto.service.VehicleService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

@Component
public class VehicleConsumer {

    private final VehicleService vehicleService;

    public VehicleConsumer(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @KafkaListener(topics = "vehicle-events", groupId = "auto-park-group")
    public void consume(ConsumerRecord<String, byte[]> record) {
        try {
            WebhookEventRequest req = WebhookEventRequest.parseFrom(record.value());

            VehicleRequest restRequest = new VehicleRequest(
                    req.getLicensePlate(),
                    req.getEventType(),
                    req.getEntryTime().isEmpty() ? null : ZonedDateTime.parse(req.getEntryTime()),
                    req.getExitTime().isEmpty() ? null : ZonedDateTime.parse(req.getExitTime()),
                    req.getLat(),
                    req.getLng()
            );

            vehicleService.processWebhookEvent(restRequest)
                    .doOnSuccess(saved -> {
                        System.out.printf("Veículo processado: %s - %s%n", saved.licensePlate(), saved.timestamp());
                    })
                    .doOnError(error -> {
                        System.err.printf("Erro ao processar veículo: %s%n", error.getMessage());
                    })
                    .subscribe();

        } catch (InvalidProtocolBufferException e) {
            System.err.printf("Erro ao parsear mensagem Protobuf: %s%n", e.getMessage());
        } catch (Exception e) {
            System.err.printf("Erro inesperado no consumidor Kafka: %s%n", e.getMessage());
        }
    }
}
