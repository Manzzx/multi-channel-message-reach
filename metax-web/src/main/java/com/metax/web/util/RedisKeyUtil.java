package com.metax.web.util;

import cn.hutool.core.util.RandomUtil;
import com.metax.common.core.constant.MetaxDataConstants;
import com.metax.common.core.utils.uuid.UUID;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static com.metax.common.core.constant.MetaxDataConstants.*;

/**
 * redisKey工具类
 *
 * @author hanabi
 * @date 2023-09-18
 */
public class RedisKeyUtil {

    //开始时间2023-09-18 13:06:24
    private static final long BEGIN_TIMESTAMP = 1695013584L;

    /**
     * 作为发送的消息的唯一id
     *
     * @return
     */
    public static Long createMessageId() {
        //生成时间戳（64位）
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime beginLocalDate = LocalDateTime.ofInstant(Instant.ofEpochSecond(BEGIN_TIMESTAMP), ZoneId.systemDefault());
        return Duration.between(beginLocalDate, now).toMillis() + RandomUtil.randomLong(100);
    }

    /**
     * 作为发送任务的id
     *
     * @return
     */
    public static Long createSendTaskId() {
        //生成时间戳（64位）
        LocalDateTime now = LocalDateTime.now();
        return now.toEpochSecond(ZoneOffset.UTC) + RandomUtil.randomLong(100);
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
