package com.metax.gateway;

import com.metax.common.core.constant.MetaxConstants;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 网关启动程序
 * 
 * @author ruoyi
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class MetaxGatewayApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(MetaxGatewayApplication.class, args);
        System.out.println("网关服务启动...\n"+ MetaxConstants.BANNER);
    }
}
