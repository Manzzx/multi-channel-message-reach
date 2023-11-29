package com.metax.web.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.metax.common.core.annotation.Excel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageTemplateVo {

    /**
     * 主键
     */
    private Long id;

    /**
     * 标题
     */
    private String name;

    /**
     * 推送类型：10.实时 20.定时
     */
    private Integer pushType;

    /**
     * 消息类型 10.通知类消息 20.营销类消息 30.验证码类消息
     */
    private Integer msgType;


    /**
     * 定时发送人群的文件路径
     */
    private String cronCrowdPath;

    /**
     * 期望发送时间
     */
    private String expectPushTime;

    /**
     * 消息发送渠道
     */
    private Integer sendChannel;

    /**
     * 消息内容 占位符用{$var}表示
     */
    private String msgContent;

    /**
     * 发送账号
     */
    private Integer sendAccount;
}
