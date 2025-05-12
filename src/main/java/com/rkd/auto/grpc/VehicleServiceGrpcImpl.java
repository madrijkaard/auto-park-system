package com.rkd.auto.grpc;

import com.rkd.auto.request.VehicleRequest;
import com.rkd.auto.service.VehicleService;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.time.ZonedDateTime;

@GrpcService
public class VehicleServiceGrpcImpl extends VehicleServiceGrpc.VehicleServiceImplBase {

    private final VehicleService vehicleService;

    public VehicleServiceGrpcImpl(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @Override
    public void sendEvent(com.rkd.auto.grpc.VehicleRequest request, StreamObserver<EmptyResponse> responseObserver) {
        VehicleRequest restRequest =
                new VehicleRequest(
                        request.getLicensePlate(),
                        request.getEventType(),
                        request.getEntryTime().isEmpty() ? null : ZonedDateTime.parse(request.getEntryTime()),
                        request.getExitTime().isEmpty() ? null : ZonedDateTime.parse(request.getExitTime()),
                        request.getLat(),
                        request.getLng()
                );

        vehicleService.processWebhookEvent(restRequest).then().subscribe(
                unused -> {
                    responseObserver.onNext(EmptyResponse.newBuilder().build());
                    responseObserver.onCompleted();
                },
                responseObserver::onError
        );
    }
}