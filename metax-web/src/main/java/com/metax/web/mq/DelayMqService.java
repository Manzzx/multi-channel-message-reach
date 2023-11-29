package com.metax.web.mq;

/**
 * @Author: hanabi
 * @DateTime: 2023/11/17 10:23
 **/
public interface DelayMqService {

    public void send(String json,String expTime);
}
