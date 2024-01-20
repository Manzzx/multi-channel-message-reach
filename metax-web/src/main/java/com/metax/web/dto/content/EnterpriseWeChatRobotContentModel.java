package com.metax.web.dto.content;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 企业微信机器人
 *
 * @Author: hanabi
 * @DateTime: 2024/1/19 15:39
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EnterpriseWeChatRobotContentModel extends ContentModel {

    /**
     * 发送消息类型
     */
    private String sendType;

    /**
     * 企业微信机器人消息支持类型：【文本消息】内容，【markdown消息】内容，【图片消息】内容，【图文消息】内容，【文件消息】内容
     */
    private String content;
}
