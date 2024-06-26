package com.metax.web.process;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.google.common.base.Throwables;
import com.metax.common.core.constant.MetaxDataConstants;
import com.metax.common.core.context.SecurityContextHolder;
import com.metax.common.core.web.domain.AjaxResult;
import com.metax.system.api.domain.SysUser;
import com.metax.web.domain.content.ProcessContent;
import com.metax.web.domain.content.SendContent;
import com.metax.web.domain.SendTaskInfo;
import com.metax.web.dto.DelayQueueTask;
import com.metax.web.mq.DelayMqService;
import com.metax.web.mq.MqService;
import com.metax.web.service.ISysUserService;
import com.metax.web.util.DataUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.metax.common.core.constant.MetaxDataConstants.*;
import static com.metax.common.core.constant.RedissonConstants.TEMPLATE_TOTAL_LOCK;
import static com.metax.common.core.constant.RedissonConstants.USER_TOTAL_LOCK;

/**
 * 发送mq处理器
 *
 * @Author: hanabi
 */
@Service
@Slf4j
public class SendMqProcess implements BusinessProcess {

    @Value("${metax.rabbitmq.delayQueues.enabled}")
    private String delayQueues;

    @Autowired
    private MqService mqService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private DataUtil dataUtil;
    @Autowired
    private DelayMqService delayMqService;
    @Autowired
    private RedissonClient redissonClient;

    @Override
    public ProcessContent process(ProcessContent context) {
        SendContent sendContext = (SendContent) context;

        try {
            String sendContextJson = JSON.toJSONString(sendContext);
            mqService.send(sendContextJson, sendContext.getSendCode());
            if (delayQueues.equals("true")) {
                sendXdl(sendContext);
            }
        } catch (Exception e) {
            sendContext.setSendLogs("errorMsg:" + Throwables.getStackTraceAsString(e));
            context.setIsNeedBreak(true);
            context.setResponse(AjaxResult.error(e.getMessage()));
            log.error("消息发送mq异常:{}", Throwables.getStackTraceAsString(e));
        } finally {
            String messageKey = sendContext.getSendTasks().get(0).getSendMessageKey();
            stringRedisTemplate.opsForList()
                    .rightPush(messageKey, JSON.toJSONString(sendContext));
            calculateNumberOfSenders(sendContext, sendContext.getSender());
            calculateNumberOfTemplate(sendContext, sendContext.getSender());
            if (TIMING.equals(sendContext.getSendTasks().get(0).getMessageTemplate().getPushType())) {
                //如果是定时任务启动发送阶段
                dataUtil.recordCronTaskStatus(CRON_TASK_SENDING, sendContext.getSendTasks().get(0).getMessageTemplate().getId(), sendContext.getSender(), "消息任务进入发送阶段，正在推送消息...");
            }
            context.setResponse(AjaxResult.success("消息发送成功"));
        }

        return context;
    }

    /**
     * 消息发送到延迟交换机
     *
     * @param sendContext
     */
    private void sendXdl(SendContent sendContext) {
        // 存放延迟队列任务
        List<DelayQueueTask> delayTasks = new ArrayList<>();
        for (SendTaskInfo sendTask : sendContext.getSendTasks()) {
            delayTasks.add(DelayQueueTask.builder().sendTaskId(sendTask.getSendTaskId())
                    .messageRedisKey(sendTask.getSendMessageKey())
                    .messageId(sendTask.getMessageId()).build());
        }
        Integer channel = sendContext.getSendTasks().get(0).getMessageTemplate().getSendChannel();
        delayMqService.send(JSON.toJSONString(delayTasks), dataUtil.getChannelConfig().channelExpTime.get(channel));
    }

    /**
     * 统计当前用总发送人数
     * 存当前用户发送人数进redis
     *
     * @param sendContext
     */
    private void calculateNumberOfSenders(SendContent sendContext, Long userId) {
        //本次任务发送总人数
        Integer sendNumber = countSendNumber(sendContext);
        RLock lock = redissonClient.getLock(USER_TOTAL_LOCK + sendContext.getSender());
        try {
            lock.lock();
            //计算该用户总发送次数
            String count = stringRedisTemplate.opsForValue().get(MetaxDataConstants.USER_SEND_NUMBER + userId);

            Integer number;
            //判断是否是第一次发送
            if (Objects.nonNull(count) && !PUSH_NOW.equals(count)) {
                //非第一次
                number = Integer.parseInt(count);
                number += sendNumber;
            } else {
                number = sendNumber;
            }
            stringRedisTemplate.opsForValue().set(MetaxDataConstants.USER_SEND_NUMBER + userId, String.valueOf(number));
        } catch (Exception e) {
            log.error("统计用户总发送人数异常:{}",Throwables.getStackTraceAsString(e));
        } finally {
            lock.unlock();
        }
    }

    /**
     * 统计当前模板被发送次数
     */
    private void calculateNumberOfTemplate(SendContent sendContext, Long userId) {
        //多少个接受者就是多少次
        Integer sendNumber = countSendNumber(sendContext);
        RLock lock = redissonClient.getLock(TEMPLATE_TOTAL_LOCK + sendContext.getSender());
        try {
            lock.lock();
            //获取本次任务消息模板id
            Long messageId = sendContext.getSendTasks().get(0).getMessageTemplate().getId();
            Map<Object, Object> entries = stringRedisTemplate.opsForHash()
                    .entries(MetaxDataConstants.TEMPLATE_SEND_NUMBER_NAME + userId);

            //判断是否是第一次发送
            if (CollectionUtil.isNotEmpty(entries)) {
                //获得该模板被发送次数

                Integer number = 0;
                //判断该模板是否是第一次发送
                if (entries.get(messageId.toString()) != null) {
                    number = Integer.valueOf(entries.get(messageId.toString()).toString());
                    number += sendNumber;
                } else {
                    number = sendNumber;
                }
                entries.put(messageId.toString(), number.toString());
                stringRedisTemplate.opsForHash()
                        .putAll(MetaxDataConstants.TEMPLATE_SEND_NUMBER_NAME + userId, entries);
            } else {
                Map<String, String> map = new HashMap<>();
                map.put(messageId.toString(), sendNumber.toString());
                stringRedisTemplate.opsForHash()
                        .putAll(MetaxDataConstants.TEMPLATE_SEND_NUMBER_NAME + userId, map);
            }
        } catch (Exception e) {
            log.error("统计模板总发送人数异常:{}",Throwables.getStackTraceAsString(e));
        } finally {
            lock.unlock();
        }


    }

    public static Integer countSendNumber(SendContent sendContext) {
        //本次任务发送总人数
        int sendNumber = 0;
        for (SendTaskInfo sendTask : sendContext.getSendTasks()) {
            sendNumber += sendTask.getReceivers().size();
        }
        return sendNumber;
    }


}
