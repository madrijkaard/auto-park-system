package com.rkd.auto.grpc;

import com.rkd.auto.service.SpotService;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class SpotServiceGrpcImpl extends SpotServiceGrpc.SpotServiceImplBase {

    private final SpotService spotService;

    public SpotServiceGrpcImpl(SpotService spotService) {
        this.spotService = spotService;
    }

    @Override
    public void getSpotStatus(SpotStatusRequest request, StreamObserver<SpotStatusResponse> responseObserver) {

        com.rkd.auto.request.SpotStatusRequest restRequest =
                new com.rkd.auto.request.SpotStatusRequest(request.getLat(), request.getLng());

        spotService.getSpotStatus(restRequest).subscribe(
                result -> {
                    SpotStatusResponse.Builder builder = SpotStatusResponse.newBuilder()
                            .setOcupied(result.ocupied())
                            .setLicensePlate(result.license_plate())
                            .setPriceUntilNow(result.price_until_now());

                    if (result.entry_time() != null)
                        builder.setEntryTime(result.entry_time().toString());

                    if (result.time_parked() != null)
                        builder.setTimeParked(result.time_parked().toString());

                    responseObserver.onNext(builder.build());
                    responseObserver.onCompleted();
                },
                responseObserver::onError
        );
    }
}
