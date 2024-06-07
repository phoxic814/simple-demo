package com.simple.grpc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
public class GrpcController {

    @Autowired
    private GrpcClientService grpcClientService;

    @PostMapping("/grpc")
    public void test() {
        List<PayRequest> list = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            PayRequest request = PayRequest.newBuilder()
                    .setOrder(UUID.randomUUID().toString())
                    .setCount(i)
                    .setAmount("1000")
                    .build();
            list.add(request);
        }

        grpcClientService.streamDoPay(list);
    }
}
