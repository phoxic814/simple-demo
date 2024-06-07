package com.simple.grpc;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class PaymentService extends PaymentServiceGrpc.PaymentServiceImplBase {

    @Override
    public void doPay(PayRequest request, StreamObserver<PayResponse> responseObserver) {
        System.out.println("doPay called with " + request);

        responseObserver.onNext(success());
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<PayRequest> doPayStream(StreamObserver<PayResponse> responseObserver) {

        return new StreamObserver<>() {

            @Override
            public void onNext(PayRequest request) {
                System.out.println("doPayStream called with " + request);

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                responseObserver.onNext(success());
            }

            @Override
            public void onError(Throwable t) {
                responseObserver.onNext(fail(t.getMessage()));
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }

    public PayResponse success() {
        return PayResponse.newBuilder()
                .setCode(1)
                .setMessage("success").build();
    }

    public PayResponse fail(String msg) {
        return PayResponse.newBuilder()
                .setCode(5)
                .setMessage(msg).build();
    }
}
