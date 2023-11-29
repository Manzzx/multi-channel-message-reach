package com.metax.system;

import com.metax.common.core.constant.MetaxConstants;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.metax.common.security.annotation.EnableCustomConfig;
import com.metax.common.security.annotation.EnableRyFeignClients;
import com.metax.common.swagger.annotation.EnableCustomSwagger2;

/**
 * 系统模块
 * 
 * @author ruoyi
 */
@EnableCustomConfig
@EnableCustomSwagger2
@EnableRyFeignClients
@SpringBootApplication
public class MetaxSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(MetaxSystemApplication.class, args);
        System.out.println("系统模块模块启动...\n"+MetaxConstants.BANNER);
    }
}
