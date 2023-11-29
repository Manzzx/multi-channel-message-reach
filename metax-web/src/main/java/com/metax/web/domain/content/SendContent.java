package com.metax.web.domain.content;

import com.metax.web.domain.SendTaskInfo;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 真正用于发送的上下文对象
 * @Author: hanabi
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SendContent extends ProcessContent {

    /**
     * 任务id
     */
    private Long sendTaskId;

    /**
     * 发送标识码 1.发送 2.撤回
     */
    private String sendCode;

    /**
     * 渠道类型
     */
    private Integer sendChannel;

    /**
     * 发送任务集合
     */
    private List<SendTaskInfo> sendTasks;

    /**
     *发送时间
     */
    private LocalDateTime sendTime;

    /**
     * 发送日志 如发送失败可存储报错信息
     */
    private String sendLogs;

    /**
     * 发送方
     */
    private Long sender;

}
