package com.metax;

import com.metax.common.core.constant.MetaxConstants;
import com.metax.common.security.annotation.EnableRyFeignClients;
import com.metax.web.mq.MqService;
import org.dromara.dynamictp.core.spring.EnableDynamicTp;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * metax核心启动类
 *
 * @author hanabi
 * @date 2023-09-08
 */
@EnableRyFeignClients
@SpringBootApplication
@EnableDynamicTp
public class MetaxWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(MetaxWebApplication.class, args);
        System.out.println(MetaxConstants.BANNER);
    }

}
