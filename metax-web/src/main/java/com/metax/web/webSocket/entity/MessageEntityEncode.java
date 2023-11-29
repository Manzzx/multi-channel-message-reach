package com.metax.web.webSocket.entity;

import com.google.gson.GsonBuilder;

import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

/**
 * 消息实体编码器
 *
 * @Author: hanabi
 * @DateTime: 2023/11/15 17:20
 **/
public class MessageEntityEncode implements Encoder.Text<MessageEntity> {

    @Override
    public String encode(MessageEntity messageEntity) {
        return new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create()
                .toJson(messageEntity);
    }

    @Override
    public void init(EndpointConfig endpointConfig) {
    }

    @Override
    public void destroy() {
    }

}
