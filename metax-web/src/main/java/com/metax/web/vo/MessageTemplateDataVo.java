package com.metax.web.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.metax.common.core.annotation.Excel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 返回前端数据展示
 * @Author: hanabi
 * @DateTime: 2023/10/4 14:56
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageTemplateDataVo {

    /**
     * 主键
     */
    private Long id;

    /**
     * 标题
     */
    private String name;

    /**
     * 当前消息状态：发送中 发送成功 发送失败
     */
    private String msgStatus;

    /**
     * 推送类型：实时 定时
     */
    private String pushType;

    /**
     * 期望发送时间
     */
    private String expectPushTime;

    /**
     * 消息发送渠道
     */
    private String sendChannel;

    /**
     * 消息内容
     */
    private String msgContent;

    /**
     * 发送账号
     */
    private Integer sendAccount;

    /**
     * 发送日志 如发送失败可存储报错信息 发送成功将返回第三方服务返回的消息id
     */
    @Excel(name = "发送日志")
    @TableField(exist = false)
    private String sendLogs;
}
