package com.metax.web.receiver;

import cn.hutool.json.JSONUtil;
import com.metax.common.core.exception.ServiceException;
import com.metax.web.dto.DelayQueueTask;
import com.metax.web.util.DataUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 延迟队列消费者
 *
 * @Author: hanabi
 * @DateTime: 2023/11/17 17:27
 **/
@Component
@Slf4j
public class RabbitMqDelayReceiver {

    @Autowired
    private DataUtil dataUtil;

    @RabbitListener(queues = "${metax.rabbitmq.delayedQueue.name}")
    public void confirmMessage(Message message) {
        List<DelayQueueTask> tasks = JSONUtil.toList(new String(message.getBody()), DelayQueueTask.class);
        //确认消息情况 消息状态已确认(发送成功或失败)就退出
        for (DelayQueueTask task : tasks) {
            dataUtil.confirmSend(null, task.getMessageId(), task.getMessageRedisKey(), task.getSendTaskId(), new ServiceException("消息发送超时，已达最大等待时间!"));
        }
    }
}
