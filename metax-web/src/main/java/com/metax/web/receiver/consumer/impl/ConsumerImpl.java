package com.metax.web.receiver.consumer.impl;

import com.metax.web.domain.content.SendContent;
import com.metax.web.domain.SendTaskInfo;
import com.metax.web.domain.Task;
import com.metax.web.receiver.consumer.ConsumerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 消费mq的消息
 * @Author: hanabi
 */
@Service
@Slf4j
public class ConsumerImpl implements ConsumerService {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private Map<Integer, ThreadPoolExecutor> dtpThreadPoolExecutors;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 使用线程池消费发送任务集合的每一个任务信息
     *
     * @param sendContext
     */
    @Override
    public void consumerSend(SendContent sendContext) {
        Integer sendChannel = sendContext.getSendChannel();
        List<SendTaskInfo> sendTasks = sendContext.getSendTasks();
        for (SendTaskInfo sendTaskInfo : sendTasks) {
            Task task = applicationContext.getBean(Task.class).setSendTaskInfo(sendTaskInfo);
            //每一个渠道消息都有对应的线程池去执行
            dtpThreadPoolExecutors.get(sendChannel).execute(task);
        }

    }

    @Override
    public void consumerRecall() {

    }
}
