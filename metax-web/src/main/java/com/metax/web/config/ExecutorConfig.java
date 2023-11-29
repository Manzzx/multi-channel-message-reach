package com.metax.web.config;

import org.dromara.dynamictp.common.em.QueueTypeEnum;
import org.dromara.dynamictp.core.DtpRegistry;
import org.dromara.dynamictp.core.support.ThreadPoolBuilder;
import org.dromara.dynamictp.core.support.ThreadPoolCreator;
import org.dromara.dynamictp.core.thread.DtpExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.metax.common.core.constant.MetaxDataConstants.DtpExecutor_SUFFIX;

/**
 * 线程池配置
 * @Author: hanabi
 */
@Configuration
public class ExecutorConfig {

    @Autowired
    private ChannelConfig channelConfig;

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 初始化渠道线程池集合 一个渠道对应一个线程池
     * @return
     */
    @Bean
    public Map<Integer, ThreadPoolExecutor> dtpThreadPoolExecutors(){
        Map<Integer,ThreadPoolExecutor> executors = new HashMap<>(32);
        for (int i = 0; i < channelConfig.channels.size(); i++) {
            executors.put(channelConfig.channels.get(i),
                    (ThreadPoolExecutor) applicationContext.getBean(channelConfig.channelNames.get(i)+DtpExecutor_SUFFIX));
        }
        return executors;
    }


}
