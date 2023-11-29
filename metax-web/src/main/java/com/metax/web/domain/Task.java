package com.metax.web.domain;

import com.metax.web.handler.ChannelHandler;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 发送任务
 * @Author: hanabi
 */
@Data
@Accessors(chain = true)//链式setter并返回对象
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class Task implements Runnable{

    private SendTaskInfo sendTaskInfo;

    @Autowired
    private Map<Integer, ChannelHandler> channelHandlers;

    @Override
    public void run() {
        //路由到对应的渠道handler
        channelHandlers.get(sendTaskInfo.getMessageTemplate().getSendChannel()).handler(sendTaskInfo);
    }
}
