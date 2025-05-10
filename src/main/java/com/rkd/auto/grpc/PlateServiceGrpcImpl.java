package com.rkd.auto.grpc;

import com.rkd.auto.service.PlateService;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class PlateServiceGrpcImpl extends PlateServiceGrpc.PlateServiceImplBase {

    private final PlateService plateService;

    public PlateServiceGrpcImpl(PlateService plateService) {
        this.plateService = plateService;
    }

    @Override
    public void getPlateStatus(PlateStatusRequest request, StreamObserver<PlateStatusResponse> responseObserver) {
        com.rkd.auto.request.PlateStatusRequest restRequest = new com.rkd.auto.request.PlateStatusRequest(
                request.getLicensePlate()
        );

        plateService.getPlateStatus(restRequest).subscribe(
                result -> {
                    PlateStatusResponse.Builder responseBuilder = PlateStatusResponse.newBuilder()
                            .setLicensePlate(result.license_plate())
                            .setPriceUntilNow(result.price_until_now());

                    if (result.entry_time() != null) {
                        responseBuilder.setEntryTime(result.entry_time().toString());
                    }

                    if (result.time_parked() != null) {
                        responseBuilder.setTimeParked(result.time_parked().toString());
                    }

                    if (result.lat() != null) {
                        responseBuilder.setLat(result.lat());
                    }

                    if (result.lng() != null) {
                        responseBuilder.setLng(result.lng());
                    }

                    responseObserver.onNext(responseBuilder.build());
                    responseObserver.onCompleted();
                },
                responseObserver::onError
        );
    }
}
