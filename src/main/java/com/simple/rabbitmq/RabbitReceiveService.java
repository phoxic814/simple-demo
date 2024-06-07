package com.simple.rabbitmq;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(value = "rabbitmq.setting", havingValue = "true")
public class RabbitReceiveService {

    @RabbitListener(queues = "MyQueue1")
    public void receive1(String msg) {
        System.out.println("收到队列1的消息 = " + msg);
    }

    @RabbitListener(queues = "MyQueue2")
    public void receive2(String msg) {
        System.out.println("收到队列2的消息 = " + msg);
    }

    @RabbitListener(
            concurrency = "10",
            bindings = {
                    @QueueBinding(
                            value = @Queue(name = "MyQueue3", durable = "true"),
                            exchange = @Exchange(name = "MyFanout1", durable = "true",type = ExchangeTypes.FANOUT)
                    )
            }
    )
    public void receive3(String msg) {
        System.out.println("收到队列3的消息,thread: " + Thread.currentThread().getId() + ",msg = " + msg);
    }

    @RabbitListener(queues = "admin")
    public void receiveAdmin(String msg) {
        System.out.println("收到队列admin的消息 = " + msg);
    }

    @RabbitListener(queues = "market")
    public void receiveMarket(String msg) {
        System.out.println("收到队列market的消息 = " + msg);
    }
}
