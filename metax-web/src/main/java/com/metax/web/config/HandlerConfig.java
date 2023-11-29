package com.metax.web.config;

import com.metax.web.handler.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;

import static com.metax.common.core.constant.MetaxDataConstants.*;

/**
 * 配置handler
 *
 * @Author: hanabi
 */
@Configuration
public class HandlerConfig implements ApplicationContextAware {

    private ApplicationContext context;

    @Autowired
    private ChannelConfig channelConfig;

    @Autowired
    private AlibabaCloudServiceSmsHandler AlibabaCloudServiceSmsHandler;

    @Autowired
    private TencentCloudServiceSmsHandler TencentCloudServiceSmsHandler;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }


    /**
     * 初始化渠道handler集合 一个渠道对应一个handler
     *
     * @return
     */
    @Bean("channelHandlers")
    public Map<Integer, ChannelHandler> channelHandlers() {
        Map<Integer, ChannelHandler> channelHandlerMap = new HashMap<>();
        //将每一个渠道handler与其渠道符绑定
        for (int i = 0; i < channelConfig.channels.size(); i++) {
            channelHandlerMap.put(channelConfig.channels.get(i), (ChannelHandler) context.getBean(channelConfig.channelNames.get(i) + HANDLER_SUFFIX));
        }
        return channelHandlerMap;
    }

    /**
     * 初始化短信渠道handler集合
     */
    @Bean("smsHandlers")
    public Map<String, ChannelHandler> smsHandlers() {
        Map<String, ChannelHandler> smsHandlerMap = new HashMap<>();
        smsHandlerMap.put(ALIBABA_CLOUD_SERVICE_SMS_NAME, AlibabaCloudServiceSmsHandler);
        smsHandlerMap.put(TENCENT_CLOUD_SERVICE_SMS_NAME, TencentCloudServiceSmsHandler);
        return smsHandlerMap;

    }
}
