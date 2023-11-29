package com.metax.web.domain;

import com.baomidou.mybatisplus.annotation.IdType;
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
 * 渠道账号对象 channel_account
 *
 * @author hanabi
 * @date 2023-09-08
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("channel_account")
public class ChannelAccount {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Excel(name = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 账号名称
     */
    @Excel(name = "账号名称")
    private String name;

    /**
     * 消息发送渠道：消息发送渠道：10.Email 20.短信 30.钉钉机器人 40.微信服务号
     */
    @Excel(name = "消息发送渠道：消息发送渠道：10.Email 20.短信 30.钉钉机器人 40.微信服务号")
    private Integer sendChannel;

    /**
     * 账号配置
     */
    @Excel(name = "账号配置")
    private String accountConfig;

    /**
     * 拥有者
     */
    @Excel(name = "拥有者")
    private String creator;

    /**
     * 创建时间
     */
    @Excel(name = "创建时间")
    @JsonFormat( pattern = "yyyy-MM-dd")
    private LocalDateTime created;

    /**
     * 更新时间
     */
    @JsonFormat( pattern = "yyyy-MM-dd")
    private LocalDateTime updated;

    /**
     * 是否删除：0.不删除 1.删除
     */
    private Integer isDeleted;


}
