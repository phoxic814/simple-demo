package com.simple.grpc;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.simple.constant.TimeUtils;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.apache.catalina.connector.Request;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class GrpcClientService {

    private final PaymentServiceGrpc.PaymentServiceStub stub;

    private final PaymentServiceGrpc.PaymentServiceBlockingStub blockingStub;

    private final PaymentServiceGrpc.PaymentServiceFutureStub futureStub;

    public GrpcClientService(ManagedChannel channel) {
        stub = PaymentServiceGrpc.newStub(channel);
        blockingStub = PaymentServiceGrpc.newBlockingStub(channel);
        futureStub = PaymentServiceGrpc.newFutureStub(channel);
    }

    public PayResponse blockDoPay(PayRequest request) {
        return blockingStub.doPay(request);
    }

    public PayResponse futureDoPay(PayRequest request) throws ExecutionException, InterruptedException {
        ListenableFuture<PayResponse> futureResponse = futureStub.doPay(request);

        Futures.addCallback(futureResponse, new FutureCallback<>() {
            public void onSuccess(PayResponse response) {
                // Handle the response
            }

            public void onFailure(Throwable t) {
                // Handle the failure
            }
        }, MoreExecutors.directExecutor());

        return futureResponse.get();
    }


    public void streamDoPay(List<PayRequest> list) {
        StreamObserver<PayRequest> requestStreamObserver = stub.doPayStream(new StreamObserver<PayResponse>() {

            @Override
            public void onNext(PayResponse response) {
                System.out.println("time:" + TimeUtils.now()+ " Response: " + response);
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("time:" + TimeUtils.now()+ " message: " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("time:" + TimeUtils.now()+ "complete");
            }
        });

        list.forEach(requestStreamObserver::onNext);
        requestStreamObserver.onCompleted();
    }
}
