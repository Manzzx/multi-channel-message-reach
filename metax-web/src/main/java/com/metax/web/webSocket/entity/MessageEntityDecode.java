package com.metax.web.webSocket.entity;

import com.google.gson.GsonBuilder;
import org.springframework.stereotype.Component;

import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

/**
 * 消息实体的解码器
 *
 * @Author: hanabi
 * @DateTime: 2023/11/15 17:18
 **/
@Component
public class MessageEntityDecode implements Decoder.Text<MessageEntity> {

    @Override
    public MessageEntity decode(String s) {
        // 利用 gson 处理消息实体，并格式化日期格式
        return new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create()
                .fromJson(s, MessageEntity.class);
    }

    @Override
    public boolean willDecode(String s) {
        return true;
    }

    @Override
    public void init(EndpointConfig endpointConfig) {
    }

    @Override
    public void destroy() {
    }

}
