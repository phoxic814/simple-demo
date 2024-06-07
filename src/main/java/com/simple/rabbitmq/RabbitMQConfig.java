package com.simple.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(value = "rabbitmq.setting", havingValue = "true")
public class RabbitMQConfig {

    // 交換機
    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange("MyFanout1");
    }

    @Bean
    public Queue queue1() {
        return new Queue("MyQueue1");
    }

    @Bean
    public Queue queue2() {
        return new Queue("MyQueue2");
    }

    @Bean
    public Queue queue3() {
        return new Queue("MyQueue3");
    }

    @Bean
    public Binding binding1(FanoutExchange fanoutExchange, Queue queue1) {
        return BindingBuilder.bind(queue1).to(fanoutExchange);
    }

    @Bean
    public Binding binding2(FanoutExchange fanoutExchange, Queue queue2) {
        return BindingBuilder.bind(queue2).to(fanoutExchange);
    }

    // direct
    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange("direct");
    }

    @Bean
    public Queue adminQueue() {
        return new Queue("admin");
    }

    @Bean
    public Queue marketQueue() {
        return new Queue("market");
    }

    @Bean
    public Binding directAdminBinding(Queue adminQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(adminQueue).to(directExchange).with("admin");
    }

    @Bean
    public Binding directMarketBinding(Queue marketQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(marketQueue).to(directExchange).with("market");
    }

    // toic
    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange("topic");
    }

}
