package com.metax.web.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.metax.common.core.annotation.Excel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


/**
 * 消息模板对象 message_template
 *
 * @author hanabi
 * @date 2023-09-08
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("message_template")
public class MessageTemplate{
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 标题
     */
    @Excel(name = "标题")
    private String name;

    /**
     * 当前消息状态：0.新建 20.停用 30.启用 40.发送中 50.发送成功 60.发送失败
     */
    private Integer msgStatus;

    /**
     * 审核状态 10.待审核 20.审核成功 30.审核不通过
     */
    private Integer auditStatus;

    /**
     * 推送类型：10.实时 20.定时
     */
    @Excel(name = "推送类型：10.实时 20.定时")
    private Integer pushType;

    /**
     * 定时任务Id (xxl-job-admin返回)
     */
    private Integer cronTaskId;

    /**
     * 定时发送人群的文件路径
     */
    @Excel(name = "定时发送人群的文件路径")
    private String cronCrowdPath;

    /**
     * 期望发送时间
     */
    @Excel(name = "期望发送时间")
    private String expectPushTime;

    /**
     * 消息发送渠道 10.Email 20.短信 30.钉钉机器人 40.微信服务号 50.push通知栏 60.飞书机器人
     */
    @Excel(name = "消息发送渠道")
    private Integer sendChannel;

    /**
     * 消息类型 10.通知类消息 20.营销类消息 30.验证码类消息
     */
    @Excel(name = " 消息类型")
    private Integer msgType;

    /**
     * 消息内容 占位符用${var}表示
     */
    @Excel(name = "消息内容 占位符用${var}表示")
    private String msgContent;

    /**
     * 发送账号
     */
    @Excel(name = "发送账号 ")
    private Long sendAccount;

    /**
     * 创建者
     */
    @Excel(name = "创建者")
    private String creator;

    /**
     * 更新者
     */
    private String updator;

    /**
     * 创建时间
     */
    @JsonFormat( pattern = "yyyy-MM-dd")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat( pattern = "yyyy-MM-dd")
    private LocalDateTime updateTime;

    /**
     * 发送日志 如发送失败可存储报错信息 发送成功将返回第三方服务返回的消息id
     */
    @Excel(name = "发送日志")
    @TableField(exist = false)
    private String sendLogs;

    /**
     * 定时模板当前使用用户
     */
    private Long currentId;

}
