package com.metax.web.webSocket.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 聊天室消息实体
 *
 * @Author: hanabi
 * @DateTime: 2023/11/15 17:19
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageEntity {

    // 发送者的 id
    private Long from;
    // 接受者的 id
    private Long to;
    // 具体信息
    private String message;
    // 发送时间
    private Date time;

}