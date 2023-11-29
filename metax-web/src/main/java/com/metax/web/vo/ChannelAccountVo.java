package com.metax.web.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.metax.common.core.annotation.Excel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 发送前端数据对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChannelAccountVo {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 账号名称
     */
    @Excel(name = "账号名称")
    private String name;

    /**
     * 消息发送渠道：消息发送渠道：10.Email 20.短信 30.钉钉机器人
     */
    @Excel(name = "消息发送渠道：消息发送渠道：10.Email 20.短信 30.钉钉机器人 ")
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
}
