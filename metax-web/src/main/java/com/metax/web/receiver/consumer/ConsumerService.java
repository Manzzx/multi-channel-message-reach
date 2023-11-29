package com.metax.web.receiver.consumer;

import com.metax.web.domain.content.SendContent;

public interface ConsumerService {

    /**
     * 消费要发送消息
     * @param sendContext
     */
    void consumerSend(SendContent sendContext);

    /**
     * 撤回 尚未实现
     */
    void consumerRecall();
}
