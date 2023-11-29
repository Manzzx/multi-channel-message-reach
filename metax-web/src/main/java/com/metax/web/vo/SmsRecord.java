package com.metax.web.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 短信回执显示对象
 * @Author: hanabi
 * @DateTime: 2023/10/30 21:46
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SmsRecord {

    /**
     * 请求id
     */
    private String requestId;

    /**
     * 短信渠道服务商名称
     */
    private String channelName;

    /**
     * 短信流水号
     */
    private String serialId;

    /**
     * 发送状态
     */
    private String status;

    /**
     * 接受者手机号
     */
    private String phone;

    /**
     * 短信模板名称
     */
    private String template;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 短信发送时间
     */
    private LocalDateTime sendDate;

    /**
     * 接受时间
     */
    private LocalDateTime receiveDate;

    /**
     * 查询时间
     */
    private LocalDateTime queryDate;

    /**
     * 返回日志/备注
     */
    private String log;

}
