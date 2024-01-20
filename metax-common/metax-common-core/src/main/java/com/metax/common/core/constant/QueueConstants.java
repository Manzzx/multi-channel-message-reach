package com.metax.common.core.constant;

/**
 * 队列参数
 *
 * @Author: hanabi
 * @DateTime: 2023/11/16 20:12
 **/
public class QueueConstants {

    /**
     * 阻塞队列最大任务数
     */
    public static final Integer BIG_QUEUE_SIZE = 1024;

    /**
     * 各消息渠道mq延迟队列过期时间 毫秒
     */
    public static final String EMAIL_EXPIRATION_TIME = "60000";

    public static final String SMS_EXPIRATION_TIME = "30000";

    public static final String DING_DING_ROBOT_EXPIRATION_TIME = "30000";

    public static final String WECHAT_SERVICE_ACCOUNT_EXPIRATION_TIME = "30000";

    public static final String PUSH_EXPIRATION_TIME = "30000";

    public static final String FEI_SHU_ROBOT_EXPIRATION_TIME = "30000";

    public static final String ENTERPRISE_WECHAT_ROBOT_TIME = "30000";
}
