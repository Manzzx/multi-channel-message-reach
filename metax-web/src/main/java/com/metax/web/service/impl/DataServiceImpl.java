package com.metax.web.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.metax.common.core.constant.HttpStatus;
import com.metax.common.core.context.SecurityContextHolder;
import com.metax.common.core.web.page.TableDataInfo;
import com.metax.system.api.domain.SysUser;
import com.metax.web.config.ChannelConfig;
import com.metax.web.domain.content.SendContent;
import com.metax.web.vo.ReceiverRecordsPage;
import com.metax.web.vo.SendTaskInfoVoPage;
import com.metax.web.service.IDataService;
import com.metax.web.util.DataUtil;
import com.metax.web.util.RedisKeyUtil;
import com.metax.web.util.RedisUtil;
import com.metax.web.vo.MessageTemplateDataVo;
import com.metax.web.vo.ReceiverRecords;
import com.metax.web.vo.SendTaskInfoVo;
import com.metax.web.xxljob.domain.CronTaskCords;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.metax.common.core.constant.MetaxDataConstants.*;

/**
 * @Author: hanabi
 * @DateTime: 2023/10/4 14:06
 **/
@Data
@Service
@Slf4j
public class DataServiceImpl implements IDataService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    public DataUtil dataUtil;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    public ChannelConfig channelConfig;
    //当前用户这天成功发送人数、失败人数和发送中
    private static final ThreadLocal<Integer> SUCCESS = ThreadLocal.withInitial(() -> 0);
    private static final ThreadLocal<Integer> FAIL = ThreadLocal.withInitial(() -> 0);
    private static final ThreadLocal<Integer> SENDING = ThreadLocal.withInitial(() -> 0);
    //指定日期的发送成功key
    private static final ThreadLocal<String> SUCCESS_KEY = ThreadLocal.withInitial(() -> "");
    private static final ThreadLocal<String> FAIL_KEY = ThreadLocal.withInitial(() -> "");
    private static final ThreadLocal<String> SENDING_KEY = ThreadLocal.withInitial(() -> "");
    //当天渠道统计情况key
    private static final ThreadLocal<String> CHANNEL_COUNT_KEY = ThreadLocal.withInitial(() -> "");
    //当天下发人数key
    private static final ThreadLocal<String> TOTAL_OF_DAY_KEY = ThreadLocal.withInitial(() -> "");
    //当天发送总人数
    public static final ThreadLocal<Integer> SEND_TOTAL_OF_DAY_TEM = ThreadLocal.withInitial(() -> 0);
    //各渠道发送人数
    private static final ThreadLocal<Map<Integer, Integer>> CHANNEL_COUNT_TEM = ThreadLocal.withInitial(HashMap::new);
    //指定日期的发送人数
    public static final ThreadLocal<Integer> SUCCESS_TEM = ThreadLocal.withInitial(() -> 0);
    public static final ThreadLocal<Integer> FAIL_TEM = ThreadLocal.withInitial(() -> 0);
    public static final ThreadLocal<Integer> SENDING_TEM = ThreadLocal.withInitial(() -> 0);
    //初始化当天渠道发送次数
    private static final ThreadLocal<Map<Integer, Integer>> CHANNEL_COUNT = ThreadLocal.withInitial(HashMap::new);


    @Override
    public SendTaskInfoVoPage getCurrentDayData(int pageNum, int pageSize, String sendMessageKey, Long userId) {
        String day = null;
        if (StrUtil.isNotBlank(sendMessageKey) && !sendMessageKey.equals("null")) {
            LocalDate date = LocalDate.parse(sendMessageKey);
            day = date.format(DateTimeFormatter.ofPattern(REDIS_DAY_KEY_FORMAT));
        } else {
            day = RedisKeyUtil.getCurrentDay();
        }
        if (userId == null || userId == 0) {
            userId = SecurityContextHolder.getUserId();
        }
        String redisKey = RedisKeyUtil.getMessageRedisKey(userId, day);
        List<SendTaskInfoVo> sendTaskInfoVos = sendContentsProcess(redisKey,userId,day);
        if (CollectionUtil.isEmpty(sendTaskInfoVos)) {
            return SendTaskInfoVoPage.builder().sendTaskInfoVos(new ArrayList<>()).total(0).build();
        }
        int start = (pageNum - 1) * pageSize;
        //取数据结束下标和集合大小之间最小的值
        int end = Math.min(start + pageSize, sendTaskInfoVos.size());
        //end是开区间
        List<SendTaskInfoVo> list = sendTaskInfoVos.subList(start, end);
        return SendTaskInfoVoPage.builder().sendTaskInfoVos(list).total(sendTaskInfoVos.size()).build();
    }

    private void generateKey(Long userId, String day) {
        SUCCESS_KEY.set(RedisKeyUtil.getSuccessRedisKey(userId, day));
        FAIL_KEY.set(RedisKeyUtil.getFailRedisKey(userId, day));
        SENDING_KEY.set(RedisKeyUtil.getSendingRedisKey(userId, day));
        CHANNEL_COUNT_KEY.set(RedisKeyUtil.getSendChannelCountRedisKey(userId, day));
        TOTAL_OF_DAY_KEY.set(RedisKeyUtil.getSendTotalOfDay(userId, day));
    }

    /**
     * 清空指定日期消息
     *
     * @param day
     * @return
     */
    @Override
    public boolean clear(String day, Long userId) {
        if (StrUtil.isBlank(day) || "null".equals(day)) {
            day = RedisKeyUtil.getCurrentDay();
        } else {
            LocalDate date = LocalDate.parse(day);
            day = date.format(DateTimeFormatter.ofPattern(REDIS_DAY_KEY_FORMAT));
        }
        if (userId == null || userId == 0) {
            userId = SecurityContextHolder.getUserId();
        }
        String redisKey = RedisKeyUtil.getMessageRedisKey(userId, day);
        Boolean delete = stringRedisTemplate.delete(redisKey);
        Boolean delChannel = stringRedisTemplate.delete(RedisKeyUtil.getSendChannelCountRedisKey(userId, day));
        if (Objects.isNull(delete)) {
            return false;
        }
        return delete.booleanValue() && delChannel.booleanValue();
    }

    /**
     * 查询定时任务记录
     *
     * @param pageNum
     * @param pageSize
     * @param messageTemplateId
     * @return
     */
    @Override
    public TableDataInfo cronTaskCordsList(int pageNum, int pageSize, String messageTemplateId) {
        List<CronTaskCords> cords = new ArrayList<>();
        Long userId = SecurityContextHolder.getUserId();
        if (StrUtil.isNotBlank(messageTemplateId)) {
            String key = RedisKeyUtil.getCronTaskCordsRedisKey(userId, messageTemplateId);
            String cord = stringRedisTemplate.opsForValue().get(key);
            if (StrUtil.isNotBlank(cord)) {
                cords.add(JSON.parseObject(stringRedisTemplate.opsForValue().get(key), CronTaskCords.class));
            }
        } else {
            List<CronTaskCords> allCronTaskCords = redisUtil.getAllCronTaskCords(userId);
            if (CollectionUtil.isNotEmpty(allCronTaskCords)) {
                cords.addAll(allCronTaskCords);
            }
        }
        int start = (pageNum - 1) * pageSize;
        //取数据结束下标和集合大小之间最小的值
        int end = Math.min(start + pageSize, cords.size());
        //end是开区间
        List<CronTaskCords> list = cords.subList(start, end);
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setRows(list);
        rspData.setMsg("查询成功");
        rspData.setTotal(cords.size());
        return rspData;
    }

    /**
     * 指定日期获取指定接受者所有消息
     *
     * @param pageNum
     * @param pageSize
     * @param receiver
     * @param sendMessageKey
     * @return
     */
    @Override
    public ReceiverRecordsPage getReceiverByDay(int pageNum, int pageSize, String receiver, String sendMessageKey) {
        String day = null;
        if (StrUtil.isNotBlank(sendMessageKey)) {
            LocalDate date = LocalDate.parse(sendMessageKey);
            day = date.format(DateTimeFormatter.ofPattern(REDIS_DAY_KEY_FORMAT));
        } else {
            day = RedisKeyUtil.getCurrentDay();
        }
        Long userId = SecurityContextHolder.getUserId();
        String redisKey = RedisKeyUtil.getMessageRedisKey(userId, day);
        List<SendTaskInfoVo> sendTaskInfoVos = sendContentsProcess(redisKey,userId,day);
        clearThreadData();
        //查出条件接受者的消息记录集合
        List<ReceiverRecords> receiverRecordsList = sendTaskInfoVos.stream()
                .filter(sendTaskInfoVo -> sendTaskInfoVo.getReceivers().contains(receiver))
                .map(sendTaskInfoVo -> ReceiverRecords.builder().receiver(receiver).sendTask(sendTaskInfoVo).build())
                .collect(Collectors.toList());

        int start = (pageNum - 1) * pageSize;
        //取数据结束下标和集合大小之间最小的值
        int end = Math.min(start + pageSize, receiverRecordsList.size());
        //end是开区间
        List<ReceiverRecords> list = receiverRecordsList.subList(start, end);
        return ReceiverRecordsPage.builder().receiverRecords(list).total(receiverRecordsList.size()).build();
    }

    /**
     * 根据用户id查询消息
     *
     * @param pageNum
     * @param pageSize
     * @param sendMessageKey
     * @param userId
     * @return
     */
    @Override
    public SendTaskInfoVoPage getUserCurrentDayData(int pageNum, int pageSize, String sendMessageKey, Long userId) {
        if (userId == null || userId == 0) {
            LambdaQueryWrapper<SysUser> wrapper = Wrappers.lambdaQuery();
            wrapper.last("LIMIT 1");
            SysUser user = dataUtil.sysUserService.getOne(wrapper);
            if (user == null) {
                return null;
            }
            userId = user.getUserId();
        }
        return getCurrentDayData(pageNum, pageSize, sendMessageKey, userId);
    }

    /**
     * 释放线程数据
     */
    @Override
    public void clearThreadData() {
        SUCCESS.remove();
        FAIL.remove();
        SENDING.remove();
        SUCCESS_KEY.remove();
        SENDING_KEY.remove();
        FAIL_KEY.remove();
        CHANNEL_COUNT_KEY.remove();
        TOTAL_OF_DAY_KEY.remove();
        SEND_TOTAL_OF_DAY_TEM.remove();
        CHANNEL_COUNT_TEM.remove();
        SUCCESS_TEM.remove();
        SENDING_TEM.remove();
        FAIL_TEM.remove();
        CHANNEL_COUNT.remove();
    }

    /**
     * 对发送任务上下文集合进行处理
     *
     * @param redisKey
     * @return
     */
    private List<SendTaskInfoVo> sendContentsProcess(String redisKey,Long userId,String day) {
        generateKey(userId, day);
        String userName = SecurityContextHolder.getUserName();
        List<String> list = stringRedisTemplate.opsForList().range(redisKey, 0, -1);
        if (CollectionUtil.isEmpty(list)) {
            recordingSuccessAndFailAndSendingNum();
            return new ArrayList<>();
        }
        //集合反转
        Collections.reverse(list);
        //存放当天下发人数
        AtomicReference<Integer> sendTotalOfDay = new AtomicReference<>();
        sendTotalOfDay.set(0);
        //初始化当天渠道发送次数
        initChannelCount();
        List<SendContent> sendContents = dataUtil.stringConvertSendContext(list);

        //返回前端最终数据集合
        List<SendTaskInfoVo> sendTaskInfoVos = new ArrayList<>();
        for (SendContent sendContent : sendContents) {
            List<SendTaskInfoVo> collect = sendContent.getSendTasks().stream().map(sendTaskInfo -> {
                //统计指定日期发送成功、失败情况和发送中
                if (MSG_SUCCESS.equals(sendTaskInfo.getMessageTemplate().getMsgStatus())) {
                    SUCCESS.set(SUCCESS.get() + sendTaskInfo.getReceivers().size());
                }
                if (MSG_FAIL.equals(sendTaskInfo.getMessageTemplate().getMsgStatus())) {
                    FAIL.set(FAIL.get() + sendTaskInfo.getReceivers().size());
                }
                if (MSG_SENDING.equals(sendTaskInfo.getMessageTemplate().getMsgStatus())) {
                    SENDING.set(SENDING.get() + sendTaskInfo.getReceivers().size());
                }
                MessageTemplateDataVo dataVo = BeanUtil
                        .copyProperties(sendTaskInfo.getMessageTemplate(), MessageTemplateDataVo.class, "pushType", "sendChannel", "msgStatus");
                //统计该用户当天下发人数
                sendTotalOfDay.set(sendTotalOfDay.get() + sendTaskInfo.getReceivers().size());
                //统计当天每一个渠道发送次数
                Integer sendChannel = sendTaskInfo.getMessageTemplate().getSendChannel();
                CHANNEL_COUNT.get().put(sendChannel, CHANNEL_COUNT.get().get(sendChannel) + sendTaskInfo.getReceivers().size());
                //将各数字类型进行映射转换
                dataVo.setMsgStatus(dataUtil.statusMapping.get(sendTaskInfo.getMessageTemplate().getMsgStatus()));
                dataVo.setExpectPushTime(StrUtil.isBlank(sendTaskInfo.getMessageTemplate().getExpectPushTime()) ||
                        PUSH_NOW.equals(sendTaskInfo.getMessageTemplate().getExpectPushTime()) ? "立即发送" : sendTaskInfo.getMessageTemplate().getExpectPushTime());
                dataVo.setSendChannel(dataUtil.channelMapping().get(sendChannel));
                dataVo.setPushType(StrUtil.isBlank(sendTaskInfo.getMessageTemplate().getExpectPushTime()) ? "实时" : "定时");
                SendTaskInfoVo sendTaskInfoVo = BeanUtil.copyProperties(sendTaskInfo, SendTaskInfoVo.class, "messageTemplate");
                sendTaskInfoVo.setSender(userName);
                sendTaskInfoVo.setMessageTemplate(dataVo);
                return sendTaskInfoVo;
            }).collect(Collectors.toList());
            sendTaskInfoVos.addAll(collect);
        }
        recordingSuccessAndFailAndSendingNum();
        recordingChannelCount();
        recordingSendTotalOfDay(sendTotalOfDay.get());
        sendTotalOfDay.set(0);

        return sendTaskInfoVos;
    }

    /**
     * 记录指定日期发送成功、失败情况和发送中
     */
    private void recordingSuccessAndFailAndSendingNum() {
        SUCCESS_TEM.set(SUCCESS.get());
        FAIL_TEM.set(FAIL.get());
        SENDING_TEM.set(SENDING.get());
        stringRedisTemplate.opsForValue().set(SUCCESS_KEY.get(), String.valueOf(SUCCESS.get()));
        stringRedisTemplate.opsForValue().set(FAIL_KEY.get(), String.valueOf(FAIL.get()));
        stringRedisTemplate.opsForValue().set(SENDING_KEY.get(), String.valueOf(SENDING.get()));
    }

    /**
     * 记录指定日期用户渠道发送情况
     */
    private void recordingChannelCount() {
        CHANNEL_COUNT_TEM.get().putAll(CHANNEL_COUNT.get());
        Set<Map.Entry<Integer, Integer>> entrySet = CHANNEL_COUNT.get().entrySet();
        //类型转换
        Map<String, String> convertedMap = new HashMap<>();
        for (Map.Entry<Integer, Integer> entry : entrySet) {
            convertedMap.put(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
        }
        stringRedisTemplate.opsForHash().putAll(CHANNEL_COUNT_KEY.get(), convertedMap);
    }

    /**
     * 记录指定日期用户下发总人数
     *
     * @param total
     */
    private void recordingSendTotalOfDay(Integer total) {
        SEND_TOTAL_OF_DAY_TEM.set(total);
        stringRedisTemplate.opsForValue().set(TOTAL_OF_DAY_KEY.get(), String.valueOf(total));
    }

    public void initChannelCount() {
        //初始化当天渠道发送次数
        channelConfig.channels.forEach(channel -> CHANNEL_COUNT.get().put(channel, 0));
    }

}
