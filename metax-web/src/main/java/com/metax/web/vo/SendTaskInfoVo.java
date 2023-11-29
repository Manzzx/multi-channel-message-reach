package com.metax.web.vo;

import com.metax.common.core.annotation.Excel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * 用于前端展示数据
 * @Author: hanabi
 * @DateTime: 2023/10/4 15:46
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendTaskInfoVo {
    /**
     * 消息id(不是模板id)
     */
    @Excel(name = "消息(子任务id)")
    private Long messageId;

    /**
     * 接受者
     */
    @Excel(name = "消息接受者")
    private Set<String> receivers;

    /**
     * 共同消息模板(占位符数据已填充完毕)
     */
    @Excel(name = "消息模板详情")
    private MessageTemplateDataVo messageTemplate;

    /**
     * 用于查询发送任务中的子任务
     */
    @Excel(name = "发送任务id")
    public Long sendTaskId ;

    /**
     * 发送阶段开始时间
     */
    private LocalDateTime sendStartTime;

    /**
     *结束时间
     */
    private LocalDateTime sendEndTime;

    /**
     * 发送阶段总耗时
     */
    private long takeTime;

    /**
     * 发送方
     */
    private String sender;

}
