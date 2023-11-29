package com.metax.web.mq.impl;

import com.metax.web.mq.MqService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static com.metax.common.core.constant.MetaxDataConstants.EXCHANGE_NAME;
import static com.metax.common.core.constant.MetaxDataConstants.TOPIC_KEY;

/**
 * rabbitmq生产者
 * @Author: hanabi
 */
@Service
@Slf4j
public class RabbitMqServiceImpl implements MqService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${metax.rabbitmq.exchange.name}")
    private String exchangeName;

    // 与queue绑定的routingKey对应
    @Value("${metax.rabbitmq.topic.name}")
    private String topicKey;

    @Override
    public void send(String json,String sendCode){
        //设置请求体信息区分是发送还是撤回
        MessagePostProcessor messagePostProcessor = message -> {
            message.getMessageProperties().setHeader("messageType", sendCode);
            return message;
        };

        rabbitTemplate.convertAndSend(exchangeName,topicKey,json,messagePostProcessor);
    }


}
