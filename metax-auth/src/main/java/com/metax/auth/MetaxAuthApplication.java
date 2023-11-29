package com.metax.auth;

import com.metax.common.core.constant.MetaxConstants;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import com.metax.common.security.annotation.EnableRyFeignClients;

/**
 * 认证授权中心
 * 
 * @author ruoyi
 */
@EnableRyFeignClients
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class MetaxAuthApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(MetaxAuthApplication.class, args);
        System.out.println("认证授权中心启动...\n"+ MetaxConstants.BANNER);
    }
}
