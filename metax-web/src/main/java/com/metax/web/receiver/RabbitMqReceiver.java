package com.metax.web.receiver;

import cn.hutool.json.JSONUtil;
import com.metax.common.core.constant.MetaxDataConstants;
import com.metax.web.domain.content.SendContent;
import com.metax.web.receiver.consumer.ConsumerService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * rabbitMq监听服务
 * @Author: hanabi
 */
@Component
public class RabbitMqReceiver {

    @Autowired
    private ConsumerService consumerService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "${spring.rabbitmq.queues}",declare = "true"),
            exchange = @Exchange(value = "${metax.rabbitmq.exchange.name}"),
            key = "${metax.rabbitmq.topic.name}"
    ))
    public void pullMessage(Message message){
        String messageType = message.getMessageProperties().getHeader("messageType");
        String msgContext = new String(message.getBody());
        if (MetaxDataConstants.SEND_CODE.equals(messageType)){
            //发送
            SendContent sendContext = JSONUtil.toBean(msgContext, SendContent.class);
            consumerService.consumerSend(sendContext);
        }else if(MetaxDataConstants.RECALL_CODE.equals(messageType)){
            //撤回
            consumerService.consumerRecall();
        }
    }

}
