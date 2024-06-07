package com.simple.rabbitmq;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(value = "rabbitmq.setting", havingValue = "true")
public class RabbitMQSendService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private FanoutExchange fanoutExchange;

    @Autowired
    private DirectExchange directExchange;

    @Autowired
    private Queue queue1;

    @Autowired
    private Queue queue3;

    public void send() {
        // 送至單點queue
        rabbitTemplate.convertAndSend(queue1.getName(), "byTopic");
        // 根據交換機binding 推送至all queue(訂閱模式)
        rabbitTemplate.convertAndSend(fanoutExchange.getName(), "", "byExchange");

        // for worker mode
        for (int i = 0; i < 10; i++) {
            rabbitTemplate.convertAndSend(queue3.getName(), "work");
        }

        // direct, by routing key
        rabbitTemplate.convertAndSend(directExchange.getName(), "admin", "direct");
    }
}
