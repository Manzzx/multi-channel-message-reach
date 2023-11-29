package com.metax.common.core.constant;

/**
 * 数据常量
 */
public class MetaxDataConstants {

    public static final String SEPARATOR = ",";

    public static final String JSON_LEFT_BRACE = "{";

    public static final String JSON_RIGHT_BRACE = "}";

    /**
     * ！！！重要配置
     */
    public static final Integer EMAIL = 10;

    public static final Integer SMS = 20;

    public static final Integer DING_DING_ROBOT = 30;

    public static final Integer WECHAT_SERVICE_ACCOUNT = 40;

    public static final Integer PUSH = 50;

    public static final Integer FEI_SHU_ROBOT = 60;

    /**
     * handler后缀
     */
    public static final String HANDLER_SUFFIX = "Handler";
    /**
     * 线程池后缀
     */
    public static final String DtpExecutor_SUFFIX = "DtpExecutor";

    /**
     * ！！！重要配置
     * 配置metax支持的消息渠道
     * SUPPORT_CHANNEL_NAME 和 SUPPORT_CHANNEL 要一一对应 如 10 -> email
     */
    public static final String SUPPORT_CHANNEL = "10,20,30,40,50,60";

    public static final String SUPPORT_CHANNEL_NAME = "email,sms,dingDingRobot,weChatServiceAccount,push,feiShuRobot";

    public static final String SUPPORT_CHANNEL_CN_NAME = "邮箱,短信,钉钉群机器人,微信公众号,APP通知栏,飞书机器人";

    /**
     * 支持的第三方短信服务名称 注意 要和对应的handler前缀对应 如:AlibabaCloudServiceSmsHandler
     */
    public static final String ALIBABA_CLOUD_SERVICE_SMS_NAME = "alibabaCloudServiceSms";
    public static final String TENCENT_CLOUD_SERVICE_SMS_NAME ="tencentCloudServiceSms";
    /**
     * 用于提取第三方短信服务名称的JASONObject key
     */
    public static final String SMS_SERVICE_KEY = "serviceName";


    /**
     * 占位符前缀
     */
    public static final String PLACE_HOLDER_PREFIX = "${";

    /**
     * 占位符后缀
     */
    public static final String PLACE_HOLDER_SUFFIX = "}";



    public static final Integer MSG_NEW = 0;

    public static final Integer MSG_STOP = 20;

    public static final Integer MSG_START = 30;

    public static final Integer MSG_SENDING = 40;

    public static final Integer MSG_SUCCESS = 50;

    public static final Integer MSG_FAIL = 60;

    public static final String SEND_CODE = "send";

    public static final String RECALL_CODE = "recall";


    public static final String EXCHANGE_NAME = "metax.point";

    public static final String TOPIC_KEY = "metax_KEY";

    public static final String ROTING_KEY = "metax_KEY";


    /**
     * redis message key前缀组成 : metax:message:userId:todayTime
     */
    public static final String APPLICATION_NAME = "metax:";

    public static final String MESSAGE_BUSINESS_NAME = APPLICATION_NAME + "message:";

    public static final String LOGIN_USER_ID = APPLICATION_NAME + "userId:";

    public static final String LOGIN_USER_NAME = APPLICATION_NAME + "userName:";

    public static final String REDIS_DAY_KEY_FORMAT = "yyyyMMdd";

    public static final String DAY_FORMAT_Y_M_D = "yyyy-MM-dd";

    public static final String DAY_FORMAT_Y_M_D_H_M_S = "yyyy-MM-dd HH:mm:ss";

    /**
     * redis template number key前缀组成 : metax:template:userId
     * 用于统计当前用户所有发送过的消息模板次数
     */
    public static final String TEMPLATE_SEND_NUMBER_NAME = APPLICATION_NAME + "template:";
    /**
     * 用于统计当前用户当天发送过的消息模板次数 ： metax:templateOfDay:useId:today
     */
    public static final String TEMPLATE_SEND_NUMBER_DAY = APPLICATION_NAME + "templateOfDay:";
    /**
     *  统计某个模板的当天发送情况 ：metax:templateCount:templateId:today    hashMap结构 包括发送成功 发送失败 发送中
     */
    public static final String TEMPLATE_COUNT_DAY = APPLICATION_NAME + "templateCount:";

    /**
     * 用户总发送次数 : metax:userTotal:userId
     */
    public static final String USER_SEND_NUMBER = APPLICATION_NAME + "userTotal:";
    /**
     * 用户发送总失败数和成功数（一个接受者为一条且只有任务进入发送阶段才会被统计） : metax:sendFail:userId:today  metax:sendSuccess:userId:today
     */
    public static final String USER_SEND_TOTAL_FAIL = APPLICATION_NAME + "sendFail:";
    public static final String USER_SEND_TOTAL_SUCCESS = APPLICATION_NAME + "sendSuccess:";
    public static final String USER_SEND_TOTAL_SENDING = APPLICATION_NAME + "sending:";
    /**
     * 用户当天发送渠道统计情况 ：metax:channelCount:userId:today
     */
    public static final String SEND_CHANNEL_COUNT = APPLICATION_NAME + "channelCount:";
    /**
     * 用户当天下发人数统计情况 ：metax:userTotalOfDay:userId:today
     */
    public static final String SEND_TOTAL = APPLICATION_NAME + "userTotalOfDay:";

    /**
     * 即时
     */
    public static final Integer REAL_TIME = 10;
    /**
     * 定时
     */
    public static final Integer TIMING = 20;

    /**
     * 用于记录当前任务第一次发送到redis的key
     */
    public static final String SEND_MESSAGE_KEY = APPLICATION_NAME + "messageRedisKey";
    /**
     * 用于当前任务暂时存进redis的sendTaskId的key
     */
    public static final String SEND_TASK_ID = APPLICATION_NAME + "sendTaskId";

    /**
     * 执行器地址注册类型
     */
    public static final Integer XXL_GROUP_TYPE_AUTO = 0;

    /**
     * 立即执行的任务 延迟时间(秒数)
     */
    public static final Integer DELAY_TIME = 10;
    /**
     * 立即执行
     */
    public static final String PUSH_NOW = "0";

    /**
     * cron时间格式
     */
    public final static String CRON_FORMAT = "ss mm HH dd MM ? yyyy";

    /**
     * 执行任务名称
     */
    public static final String JOB_HANDLER_NAME = "metaxJob";

    /**
     * 超时时间
     */
    public static final Integer TIME_OUT = 120;

    /**
     * 失败重试次数
     */
    public static final Integer RETRY_COUNT = 0;

    /**
     * 调度状态：0-停止，1-运行
     */
    public final static Integer TRIGGER_STATUS_TRUE = 1;
    public final static Integer TRIGGER_STATUS_FALSE = 0;

    /**
     * 邮件
     */
    public final static String NOTING_EMAIL = "hanabizzx7880@163.com";

    /**
     * 定时人群文件接受者表头名称
     */
    public static final String CRON_FILE_RECEIVER = "receiver";

    /**
     * 记录定时任务最近一次发送状态 :任务开始 启动中 发送中 失败 发送成功 暂停
     */
    public static final String CRON_TASK_STARTING = "任务开始";
    public static final String CRON_TASK_SCHEDULING = "启动中";
    public static final String CRON_TASK_SENDING = "发送中";
    public static final String CRON_TASK_SUCCESS = "发送成功";
    public static final String CRON_TASK_STOP = "已暂停";
    public static final String CRON_TASK_FAIL = "发送失败";
    /**
     * 记录定时任务最近一次发送状态redisKey : metax:cronTaskStatus:userId:messageTemplateId
     */
    public static final String CRON_TASK_STATUS_KEY = APPLICATION_NAME + "cronTaskStatus:";

    public static final String OK = "OK";
    /**
     * redis最大模糊查询key个数
     */
    public static final Integer KEYS_SEARCH_MAX_VALUE = 10000;


    public static final Integer AUDIT_WAITING = 10;

    public static final Integer AUDIT_PASS = 20;

    public static final Integer AUDIT_REJECTED = 30;

    /**
     * 允许使用定时模板
     */
    public static final String PERMITTED_USE = "-1";

    /**
     * 用户近期短信回执查询记录 metax:sms_records:userId
     */
    public static final String SMS_RECORDS_KEY = APPLICATION_NAME+"sms_records:";

    /**
     * 近期短信回执记录设置一个月后过期 单位秒
     */
    public static final long SMS_RECORD_EXPIRE_TIME = 2592000;

    public static final String TALK_KEY = APPLICATION_NAME+"talk:";

    /**
     * 分布式锁 ： metax:lock:sendContentId 粒度为锁住同一发送任务
     */
    public static final String SEND_CONTENT_LOCK = APPLICATION_NAME + "lock:";
    /**
     * 获取锁最大等待时间
     */
    public static final Long TRY_LOCK_WAIT_TIME = 10L;
    /**
     * 获取锁最大
     */
    public static final Long LOCK_TIME = 10L;

}
