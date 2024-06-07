package com.simple.rabbitmq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ConditionalOnProperty(value = "rabbitmq.setting", havingValue = "true")
public class RabbitController {

    @Autowired
    private RabbitMQSendService rabbitMQSendService;

    @GetMapping("rabbit/send")
    public void send() {
        rabbitMQSendService.send();
    }
}
