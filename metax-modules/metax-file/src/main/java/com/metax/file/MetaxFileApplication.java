package com.metax.file;

import com.metax.common.core.constant.MetaxConstants;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import com.metax.common.swagger.annotation.EnableCustomSwagger2;

/**
 * 文件服务
 * 
 * @author ruoyi
 */
@EnableCustomSwagger2
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class MetaxFileApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(MetaxFileApplication.class, args);
        System.out.println("文件服务启动...\n"+MetaxConstants.BANNER);
    }
}
