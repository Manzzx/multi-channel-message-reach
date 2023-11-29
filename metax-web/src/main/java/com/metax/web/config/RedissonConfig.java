package com.metax.web.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson分布式锁配置
 *
 * @Author: hanabi
 * @DateTime: 2023/11/20 8:58
 **/
@Configuration
public class RedissonConfig {

    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private String redisPort;

    @Value("${spring.redis.auth}")
    private String auth;


    @Bean
    public RedissonClient redissonClient(){
        //配置
        Config config = new Config();
        //设置配置信息
        String address = "redis://"+redisHost+":"+redisPort;
        config.useSingleServer().setAddress(address);
        //创建RedissonClient对象
        return Redisson.create(config);
    }
}
