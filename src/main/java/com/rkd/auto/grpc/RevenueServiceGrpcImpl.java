package com.rkd.auto.grpc;

import com.rkd.auto.service.RevenueService;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.time.LocalDate;

@GrpcService
public class RevenueServiceGrpcImpl extends RevenueServiceGrpc.RevenueServiceImplBase {

    private final RevenueService revenueService;

    public RevenueServiceGrpcImpl(RevenueService revenueService) {
        this.revenueService = revenueService;
    }

    @Override
    public void getRevenue(RevenueRequest request, StreamObserver<RevenueResponse> responseObserver) {

        com.rkd.auto.request.RevenueRequest restRequest = new com.rkd.auto.request.RevenueRequest(LocalDate.parse(request.getDate()), request.getSector());

        revenueService.getRevenue(restRequest).subscribe(
                result -> {
                    RevenueResponse response = RevenueResponse.newBuilder()
                            .setAmount(result.amount())
                            .setCurrency(result.currency())
                            .setTimestamp(result.timestamp().toString())
                            .build();

                    responseObserver.onNext(response);
                    responseObserver.onCompleted();
                },
                responseObserver::onError
        );
    }
}
