package com.metax.web.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.metax.common.core.exception.ServiceException;
import com.metax.system.api.domain.SysUser;
import com.metax.web.config.ChannelConfig;
import com.metax.web.domain.MessageTemplate;
import com.metax.web.domain.SendTaskInfo;
import com.metax.web.domain.content.SendContent;
import com.metax.web.service.ISysUserService;
import com.metax.web.xxljob.domain.CronTaskCords;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.metax.common.core.constant.SendMessageTypeConstants.*;
import static com.metax.common.core.constant.MetaxDataConstants.*;

/**
 * 消息数据统计/记录工具类
 *
 * @Author: hanabi
 */
@Component
@Slf4j
@Data
public class DataUtil {

    @Autowired
    public ChannelConfig channelConfig;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    public ISysUserService sysUserService;
    @Autowired
    private RedissonClient redissonClient;

    public Map<Integer, String> statusMapping;

    public Map<String, String> sendTypeMapping;
    //初始化当天渠道发送次数
    public Map<Integer, Integer> channelCount = new HashMap<>();


    /**
     * 渠道映射 Integer to String
     *
     * @return
     */
    public Map<Integer, String> channelMapping() {
        Map<Integer, String> map = new HashMap<>();
        for (int i = 0; i < channelConfig.channels.size(); i++) {
            map.put(channelConfig.channels.get(i), channelConfig.channelNames.get(i));
        }
        return map;
    }

    /**
     * 渠道映射  String to Integer
     *
     * @return
     */
    public Map<String, Integer> channelMappingToInteger() {
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < channelConfig.channels.size(); i++) {
            map.put(channelConfig.channelCNNames.get(i),channelConfig.channels.get(i));
        }
        return map;
    }

    /**
     * 渠道中文映射
     *
     * @return
     */
    public Map<Integer, String> channelCNMapping() {
        Map<Integer, String> map = new HashMap<>();
        for (int i = 0; i < channelConfig.channels.size(); i++) {
            map.put(channelConfig.channels.get(i), channelConfig.channelCNNames.get(i));
        }
        return map;
    }

    /**
     * 类型映射集合
     *
     * @return
     */
    @PostConstruct
    public void typeMapping() {
        Map<Integer, String> statusMap = new HashMap<>();
        Map<String, String> sendTypeMap = new HashMap<>();

        statusMap.put(MSG_NEW, "正常");
        statusMap.put(MSG_STOP, "已停用");
        statusMap.put(MSG_START, "已启用");
        statusMap.put(MSG_SENDING, "发送中");
        statusMap.put(MSG_FAIL, "发送失败");
        statusMap.put(MSG_SUCCESS, "发送成功");
        sendTypeMap.put(TEXT, TEXT_NAME);
        sendTypeMap.put(LINK, LINK_NAME);
        sendTypeMap.put(MARKDOWN, MARKDOWN_NAME);
        sendTypeMap.put(ACTION_CARD, ACTION_CARD_NAME);
        sendTypeMap.put(FEED_CARD, FEED_CARD_NAME);

        //初始化当天渠道发送次数
        initChannelCount();
        this.statusMapping = statusMap;
        this.sendTypeMapping = sendTypeMap;
    }

    public void initChannelCount() {
        //初始化当天渠道发送次数
        channelConfig.channels.forEach(channel -> channelCount.put(channel, 0));
    }

    /**
     * 记录定时任务模板最近一次发送状态
     *
     * @param nextStatus
     * @param messageTemplateId
     * @param log
     */
    public void recordCronTaskStatus(String nextStatus, Long messageTemplateId, Long sender, String log) {
        CronTaskCords cronTaskCords = JSONUtil.toBean(stringRedisTemplate
                .opsForValue().get(CRON_TASK_STATUS_KEY + sender + ":" + messageTemplateId), CronTaskCords.class);

        if (Objects.isNull(cronTaskCords)) {
            throw new ServiceException("非法操作用户");
        }
        LocalDateTime now = LocalDateTime.now();
        if (CRON_TASK_SCHEDULING.equals(nextStatus)) {
            //调度开始 开始启动阶段
            cronTaskCords.setSchedulingTime(now);
            //将上一次任务的信息删除
            cronTaskCords.setSendTakeTime(0);
            cronTaskCords.setStartTakeTime(0);
            cronTaskCords.setSendingTime(null);
            cronTaskCords.setFailTime(null);
            cronTaskCords.setTotalTakeTime(new BigInteger("0"));
            cronTaskCords.setSuccessTime(null);
        }

        if (CRON_TASK_SENDING.equals(nextStatus)) {
            //开始发送阶段 计算启动阶段花费时间=调度开始时间-当前时间
            cronTaskCords.setStartTakeTime(Duration.between(cronTaskCords.getSchedulingTime(), now).toMillis());
            cronTaskCords.setSendingTime(now);
        }

        if (CRON_TASK_SUCCESS.equals(nextStatus)) {
            //发送完成阶段 计算发送阶段花费时间 发送任务可能有多条消息会把同一发送任务的其他消息成功状况覆盖
            cronTaskCords.setSendTakeTime(Duration.between(cronTaskCords.getSendingTime(), now).toMillis());
            cronTaskCords.setSuccessTime(now);
            //计算总耗时
            BigInteger total = new BigInteger(Long.toString(cronTaskCords.getStartTakeTime())).add(new BigInteger(Long.toString(cronTaskCords.getSendTakeTime())));
            cronTaskCords.setTotalTakeTime(total);
        }

        if (CRON_TASK_FAIL.equals(nextStatus)) {
            //发送失败清除发送花费时间
            cronTaskCords.setFailTime(now);
        }

        if (CRON_TASK_STOP.equals(nextStatus)) {
            //暂停
            cronTaskCords.setStopTime(now);
        }

        //设置阶段状态
        cronTaskCords.setStatus(nextStatus);
        if (StrUtil.isNotBlank(log)) {
            cronTaskCords.setLog(log);
        }
        //存进redis
        stringRedisTemplate.opsForValue()
                .set(CRON_TASK_STATUS_KEY + cronTaskCords.getSender() + ":" + messageTemplateId, JSON.toJSONString(cronTaskCords));
    }


    /**
     * 确认发送任务的某一组消息的发送状态
     *
     * @param sendId
     * @param messageId
     */
    public synchronized void confirmSend(String sendId, Long messageId, String messageRedisKey, Long sendTaskId, Exception ex) {
        //获取本次发送任务的redisKey
        if (StrUtil.isBlank(messageRedisKey)) {
            throw new ServiceException(SEND_MESSAGE_KEY + "is null");
        }
        RLock rLock = redissonClient.getLock(SEND_CONTENT_LOCK + sendTaskId);;
        try {
            rLock.lock();
            List<SendContent> sendContexts =
                    stringConvertSendContext(Objects.requireNonNull(stringRedisTemplate.opsForList()
                            .range(messageRedisKey, 0, -1)));
            updateMsgStatus(sendContexts, messageId, sendId, messageRedisKey, sendTaskId, ex);
        } catch (Exception e) {
            log.error("发送流程出现异常", e);
        } finally {
            rLock.unlock();
        }
    }

    /**
     * 更新消息发送状态
     *
     * @param sendContexts
     * @param messageId
     * @param sendId
     */
    public void updateMsgStatus(List<SendContent> sendContexts, Long messageId, String sendId, String messageRedisKey, Long sendTaskId, Exception ex) {
        if (sendTaskId == null || sendTaskId == 0) {
            throw new ServiceException(SEND_TASK_ID + "is null");
        }
        for (SendContent sendContext : sendContexts) {
            //从当天发送任务集合中筛选出本次发送任务
            if (Objects.equals(sendContext.getSendTaskId(), sendTaskId)) {
                //提前获取出本次任务的下标
                int sendContextIndex = sendContexts.indexOf(sendContext);
                //获取当前子任务
                List<SendTaskInfo> sendTasks = sendContext.getSendTasks();
                for (SendTaskInfo sendTask : sendTasks) {
                    if (Objects.equals(sendTask.getMessageId(), messageId)) {
                        MessageTemplate messageTemplate = sendTask.getMessageTemplate();
                        //如果不是待确认状态就退出本次消息确认
                        if (!MSG_SENDING.equals(messageTemplate.getMsgStatus())) {
                            return;
                        }
                        //修改发送状态
                        if (StrUtil.isNotBlank(sendId)) {
                            //成功
                            messageTemplate.setMsgStatus(MSG_SUCCESS);
                            messageTemplate.setSendLogs("消息发送成功,返回信息:" + sendId);
                            LocalDateTime now = LocalDateTime.now();
                            sendTask.setSendEndTime(now);
                            sendTask.setTakeTime(Duration.between(sendTask.getSendStartTime(), now).toMillis());
                            if (TIMING.equals(messageTemplate.getPushType())) {
                                //定时任务完成记录
                                recordCronTaskStatus(CRON_TASK_SUCCESS, messageTemplate.getId(), sendContext.getSender(), "消息发送成功,返回信息:" + sendId);
                            }
                            sendTask.setMessageTemplate(messageTemplate);
                        } else {
                            //失败
                            messageTemplate.setMsgStatus(MSG_FAIL);
                            messageTemplate.setSendLogs("消息发送失败,返回信息:" + ex.getMessage());
                            LocalDateTime now = LocalDateTime.now();
                            sendTask.setSendEndTime(now);
                            sendTask.setTakeTime(Duration.between(sendTask.getSendStartTime(), now).toMillis());
                            if (TIMING.equals(messageTemplate.getPushType())) {
                                //定时任务冗余失败记录
                                recordCronTaskStatus(CRON_TASK_FAIL, messageTemplate.getId(), sendContext.getSender(), ex.getMessage());
                            }
                            sendTask.setMessageTemplate(messageTemplate);
                        }
                    }
                }
                //重新存进redis
                stringRedisTemplate.opsForList().set(messageRedisKey, sendContextIndex, JSON.toJSONString(sendContext));
            }
        }
    }

    /**
     * 将redis取出来的string类型集合转换成SendContext类型
     *
     * @param list
     * @return
     */
    public List<SendContent> stringConvertSendContext(List<String> list) {
        return list.stream().map(s -> JSONUtil.toBean(s, SendContent.class)).collect(Collectors.toList());
    }

    /**
     * 转换成String类型
     *
     * @param list
     * @return
     */
    public List<String> sendContextConvertString(List<SendContent> list) {
        return list.stream().map(JSON::toJSONString).collect(Collectors.toList());
    }

    /**
     * 出现未知问题废弃,别处已实现
     * 记录用户发送失败消息(人数)数量
     */
    public void RecordingFail(String userName, SendTaskInfo sendTask) {
        Long userId = getSender(userName);
        String total = stringRedisTemplate.opsForValue().get(USER_SEND_TOTAL_FAIL + userId);
        int num = 0;
        if (StrUtil.isBlank(total)) {
            //如果是第一次记录
            num += sendTask.getReceivers().size();
        } else {
            num = Integer.parseInt(total);
            num += sendTask.getReceivers().size();
        }
        stringRedisTemplate.opsForValue().set(USER_SEND_TOTAL_FAIL + userId, String.valueOf(num));
    }

    /**
     * 出现未知问题废弃,别处已实现
     * 记录用户发送成功消息(人数)数量
     */
    public void RecordingSuccess(String userName, SendTaskInfo sendTask) {
        Long userId = getSender(userName);
        String total = stringRedisTemplate.opsForValue().get(USER_SEND_TOTAL_SUCCESS + userId);
        int num = 0;
        if (StrUtil.isBlank(total)) {
            //如果是第一次记录
            num += sendTask.getReceivers().size();
        } else {
            num = Integer.parseInt(total);
            num += sendTask.getReceivers().size();
        }
        stringRedisTemplate.opsForValue().set(USER_SEND_TOTAL_SUCCESS + userId, String.valueOf(num));
    }

    public Long getSender(String userName) {
        return sysUserService.lambdaQuery().eq(SysUser::getUserName, userName).one().getUserId();

    }

    public Integer countSendNumber(SendContent sendContext) {
        //本次任务发送总人数
        Integer sendNumber = 0;
        for (SendTaskInfo sendTask : sendContext.getSendTasks()) {
            sendNumber += sendTask.getReceivers().size();
        }
        return sendNumber;
    }


}
