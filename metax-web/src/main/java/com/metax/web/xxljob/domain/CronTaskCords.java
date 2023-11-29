package com.metax.web.xxljob.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * 定时任务发送状态记录 用于链路追踪 启动阶段 发送阶段 并记录各个状态开始时间
 * 两个阶段 启动中 发送中
 * 六个状态 任务开始 启动 发送 失败 发送成功 暂停
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CronTaskCords {

    /**
     * 模板id
     */
    private Long messageTemplateId;

    /**
     * 消息发送渠道
     */
    private String sendChannel;

    /**
     * 用户id
     */
    private Long sender;

    /**
     * 期望发送时间
     */
    private String expectPushTime;

    /**
     * 状态 任务开始 启动 发送 失败 发送成功 暂停 注意:一旦进入发送阶段无法停止
     */
    private String status;

    /**
     * 发送日志信息
     */
    private String log;

    /**
     * 任务开始时间
     */
    private LocalDateTime startTime;

    /**
     * 最近一次调度(启动)开始时间 用于计算启动阶段时间
     */
    private LocalDateTime schedulingTime;

    /**
     * 最近一次发送开始时间
     */
    private LocalDateTime sendingTime;

    /**
     * 最近一次任务停止时间
     */
    private LocalDateTime stopTime;

    /**
     * 最近一次消息发送成功时间
     */
    private LocalDateTime successTime;

    /**
     * 最近一次发送失败时间
     */
    private LocalDateTime failTime;

    /**
     * xxl启动任务阶段花费时间 = 发送mq前的时间
     */
    private long startTakeTime;

    /**
     * 发送阶段花费时间
     */
    private long sendTakeTime;

    /**
     * 总花费时间
     */
    private BigInteger totalTakeTime;
}
