package com.metax.web.mq.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 延迟队列配置
 *
 * @Author: hanabi
 * @DateTime: 2023/11/17 9:51
 **/
@Configuration
@ConditionalOnProperty(name = "metax.rabbitmq.delayQueues.enabled", havingValue = "true")
public class RabbitMqConfig {

    /**
     * 延迟队列交换机
     */
    @Value("${metax.rabbitmq.delayedExchange.name}")
    private String delayedExchange;
    /**
     * 延迟队列
     */
    @Value("${metax.rabbitmq.delayedQueue.name}")
    private String delayedQueue;
    /**
     * 延迟队列key
     */
    @Value("${metax.rabbitmq.delayedRouting.key}")
    private String delayedRoutingKey;

    /**
     * 延迟队列
     *
     * @return
     */
    @Bean
    public Queue delayedQueue() {
        return new Queue(delayedQueue, true, false, false);
    }


    /**
     * 延迟交换机
     *
     * @return
     */
    @Bean
    public Exchange delayedExchange() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-delayed-type", "direct");
        Exchange exchange = new CustomExchange(
                delayedExchange,
                "x-delayed-message",
                true,
                false,
                arguments
        );
        return exchange;
    }


    /**
     * 将延迟队列和延迟交换机绑定
     *
     * @param delayedQueue
     * @param delayedExchange
     * @return
     */
    @Bean
    public Binding bindingDelayedQueueToExchange(Queue delayedQueue, Exchange delayedExchange) {
        return BindingBuilder.bind(delayedQueue).to(delayedExchange).with(delayedRoutingKey).noargs();
    }


}
