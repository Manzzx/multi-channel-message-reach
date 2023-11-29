package com.metax.web.mq.impl;

import com.metax.web.mq.DelayMqService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


/**
 * 发送消息到延迟交换机
 *
 * @Author: hanabi
 * @DateTime: 2023/11/17 10:23
 **/
@Service
@Slf4j
public class DelayMqServiceImpl implements DelayMqService {

    /**
     * 延迟队列交换机
     */
    @Value("${metax.rabbitmq.delayedExchange.name}")
    private String delayedExchange;
    /**
     * 延迟队列key
     */
    @Value("${metax.rabbitmq.delayedRouting.key}")
    private String delayedRoutingKey;

    @Autowired
    private AmqpTemplate rabbitTemplate;

    @Override
    public void send(String json, String expTime) {

        rabbitTemplate.convertAndSend(delayedExchange, delayedRoutingKey, json,
                message -> {
                    //设置消息持久化
                    message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                    message.getMessageProperties().setHeader("x-delay",expTime);//设置延时时间
                    return message;
                });


    }
}
