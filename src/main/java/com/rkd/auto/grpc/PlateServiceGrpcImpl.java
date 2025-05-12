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
                            .setLicensePlate(result.licensePlate())
                            .setPriceUntilNow(result.priceUntilNow());

                    if (result.entryTime() != null) {
                        responseBuilder.setEntryTime(result.entryTime().toString());
                    }

                    if (result.timeParked() != null) {
                        responseBuilder.setTimeParked(result.timeParked().toString());
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
