package com.metax.gen;

import com.metax.common.core.constant.MetaxConstants;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.metax.common.security.annotation.EnableCustomConfig;
import com.metax.common.security.annotation.EnableRyFeignClients;
import com.metax.common.swagger.annotation.EnableCustomSwagger2;

/**
 * 代码生成
 * 
 * @author ruoyi
 */
@EnableCustomConfig
@EnableCustomSwagger2
@EnableRyFeignClients
@SpringBootApplication
public class MetaxGenApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(MetaxGenApplication.class, args);
        System.out.println("代码生成模块启动...\n"+ MetaxConstants.BANNER);
    }
}
