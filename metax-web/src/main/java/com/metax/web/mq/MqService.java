package com.metax.web.mq;

public interface MqService {

    /**
     * @param json     发送任务上下文json
     * @param sendCode 发送还是撤回
     */
    public void send(String json, String sendCode);
}
