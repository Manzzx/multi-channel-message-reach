package com.metax.web.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 响应到前端对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageTemplateDto {


    private Long id;


    private String name;

    /**
     * 当前消息状态：0.新建 20.停用 30.启用 40.发送中 50.发送成功 60.发送失败
     */
    private String msgStatus;

    /**
     * 与messageTemplate的区别
     */
    private String pushType;

    /**
     * 消息类型 10.通知类消息 20.营销类消息 30.验证码类消息
     */
    private Integer msgType;

    /**
     * 审核状态 10.待审核 20.审核成功 30.审核不通过
     */
    private Integer auditStatus;

    /**
     * 定时发送人群的文件路径
     */
    private String cronCrowdPath;

    /**
     * 期望发送时间
     */
    private String expectPushTime;

    /**
     * 与messageTemplate的区别
     */
    private String sendChannel;

    /**
     * 消息内容 占位符用{$var}表示
     */
    private String msgContent;

    /**
     * 发送账号
     */
    private Integer sendAccount;

    /**
     * 创建者
     */
    private String creator;

    /**
     * 更新者
     */
    private String updator;

    /**
     * 创建时间
     */
    private String createTime;

}
