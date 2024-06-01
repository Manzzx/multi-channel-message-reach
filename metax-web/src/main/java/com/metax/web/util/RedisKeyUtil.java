package com.metax.web.util;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import com.metax.common.core.constant.MetaxDataConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

import static com.metax.common.core.constant.MetaxDataConstants.*;

/**
 * redisKey工具类
 *
 * @author hanabi
 * @date 2023-09-18
 */
@Component
public class RedisKeyUtil {

    //用来生成当天消息的递增id key:metax:daily_msg_id:messageTemplateId
    private static final String DAILY_MSG_KEY = APPLICATION_NAME + "daily_msg_id:";
    ////用来生成当天消息任务的递增id key:metax:daily_task_id
    private static final String DAILY_TASK_KEY = APPLICATION_NAME + "daily_task_id";
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 生成消息id
     * @param id
     * @return
     */
    public long createMessageId(Long id) {
        String key = DAILY_MSG_KEY + id;
        Long increment = stringRedisTemplate.opsForValue().increment(key);
        if (1 == increment){
            // 设置 ID 为每天 0 点过期
            stringRedisTemplate.expireAt(key, LocalDate.now().atStartOfDay().plusDays(1).atZone(ZoneId.systemDefault()).toInstant());
        }
        //以消息模板+当天发送次数作为id
        StringBuilder stringBuilder = new StringBuilder();
        String strId = stringBuilder.append(id).append(increment).toString();
        return Long.parseLong(strId);
    }

    /**
     * 作为发送任务的id
     *
     * @return
     */
    public long createSendTaskId() {
        Long increment = stringRedisTemplate.opsForValue().increment(DAILY_TASK_KEY);
        if (1 == increment){
            // 设置 ID 为每天 0 点过期
            stringRedisTemplate.expireAt(DAILY_TASK_KEY, LocalDate.now().atStartOfDay().plusDays(1).atZone(ZoneId.systemDefault()).toInstant());
        }
        return increment;
    }

    /**
     * 获取redisMessageKey 前缀组成 : metax:message:userId:todayTime
     *
     * @param userId
     * @return
     */
    public static String createMessageRedisKey(Long userId) {
        return MetaxDataConstants.MESSAGE_BUSINESS_NAME + userId.toString() + ":" + getCurrentDay();
    }

    /**
     * 获取当前年月日 如 2023918
     *
     * @return
     */
    public static String getCurrentDay() {
        LocalDateTime currentDate = LocalDateTime.now();
        // 生成当天日期格式
        return currentDate.format(DateTimeFormatter.ofPattern(REDIS_DAY_KEY_FORMAT));
    }

    /**
     * 获取指定用户指定日期key 精确到日
     *
     * @param userId
     * @param localDateTime
     * @return
     */
    public static String getMessageRedisKey(Long userId, String localDateTime) {
        return MetaxDataConstants.MESSAGE_BUSINESS_NAME + userId.toString() + ":" + localDateTime;
    }

    /**
     * 获取指定用户发送成功消息指定日期key 精确到日
     *
     * @param userId
     * @param localDateTime
     * @return
     */
    public static String getSuccessRedisKey(Long userId, String localDateTime) {
        return MetaxDataConstants.USER_SEND_TOTAL_SUCCESS + userId.toString() + ":" + localDateTime;
    }

    /**
     * 获取指定用户发送成功消息指定日期key 精确到日
     *
     * @param userId
     * @param localDateTime
     * @return
     */
    public static String getFailRedisKey(Long userId, String localDateTime) {
        return MetaxDataConstants.USER_SEND_TOTAL_FAIL + userId.toString() + ":" + localDateTime;
    }

    /**
     * 获取指定用户发送中消息指定日期key 精确到日
     *
     * @param userId
     * @param localDateTime
     * @return
     */
    public static String getSendingRedisKey(Long userId, String localDateTime) {
        return MetaxDataConstants.USER_SEND_TOTAL_SENDING + userId.toString() + ":" + localDateTime;
    }

    /**
     * 获取指定用户指定日期发送渠道统计情况key
     *
     * @param userId
     * @param localDateTime
     * @return
     */
    public static String getSendChannelCountRedisKey(Long userId, String localDateTime) {
        return MetaxDataConstants.SEND_CHANNEL_COUNT + userId.toString() + ":" + localDateTime;
    }

    /**
     * 获取指定模板指定日期的发送情况
     *
     * @param id  模板id
     * @param day
     * @return
     */
    public static String getTemplateCountOfDayRedisKey(Long id, String day) {
        return TEMPLATE_COUNT_DAY + id + ":" + day;
    }


    /**
     * 获取指定定时任务记录key
     *
     * @param userId
     * @param id
     * @return
     */
    public static String getCronTaskCordsRedisKey(Long userId, String id) {
        return CRON_TASK_STATUS_KEY + userId + ":" + id;
    }

    /**
     * 获取指定用户指定日期的下发总人数key
     *
     * @param userId
     * @param localDateTime
     * @return
     */
    public static String getSendTotalOfDay(Long userId, String localDateTime) {
        return SEND_TOTAL + userId + ":" + localDateTime;
    }

}
