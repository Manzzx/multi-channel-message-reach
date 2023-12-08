package com.metax.common.core.constant;

import static com.metax.common.core.constant.MetaxDataConstants.APPLICATION_NAME;

/**
 * Redisson常量
 * @Author: hanabi
 * @DateTime: 2023/12/8 13:07
 **/
public class RedissonConstants {

    /**
     * 分布式锁 ： metax:msgLock:sendContentId 粒度为锁住同一发送任务
     */
    public static final String SEND_CONTENT_LOCK = APPLICATION_NAME + "msgLock:";
    /**
     * 获取锁最大等待时间
     */
    public static final Long TRY_LOCK_WAIT_TIME = 10L;
    /**
     * 获取锁最大
     */
    public static final Long LOCK_TIME = 10L;

    /**
     * 用户总发送人数分布式锁key metax:userTotalLock:userId 粒度为锁住同一用户（发送方）
     */
    public static final String USER_TOTAL_LOCK = APPLICATION_NAME + "userTotalLock:";

    /**
     * 模板总发送人数分布式锁key metax:templateTotalLock:userId 粒度为锁住同一用户（发送方）
     */
    public static final String TEMPLATE_TOTAL_LOCK = APPLICATION_NAME + "templateTotalLock:";
}
