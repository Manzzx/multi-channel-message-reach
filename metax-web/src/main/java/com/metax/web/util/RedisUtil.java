package com.metax.web.util;

import com.alibaba.fastjson2.JSON;
import com.metax.web.xxljob.domain.CronTaskCords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static com.metax.common.core.constant.MetaxDataConstants.*;

/**
 * redis工具类
 *
 * @Author: hanabi
 * @DateTime: 2023/10/8 0:24
 **/
@Component
public class RedisUtil {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 获取当前用户的所有定时任务记录 模糊查询
     *
     * @param userId
     * @return
     */
    public List<CronTaskCords> getAllCronTaskCords(Long userId) {
        String patternKey = CRON_TASK_STATUS_KEY + userId + ":*";
        //存放所有符合的key集合
        Set<String> sKeys = new LinkedHashSet<>();
        //设置回调函数
        stringRedisTemplate.execute((RedisConnection connection) -> {
            try (Cursor<byte[]> cursor = connection.scan(
                    ScanOptions.scanOptions()
                            //这里指定每次扫描key的数量
                            .count(KEYS_SEARCH_MAX_VALUE)
                            .match(patternKey)
                            .build()
            )) {
                //遍历加入集合
                cursor.forEachRemaining(item -> sKeys.add(RedisSerializer.string().deserialize(item)));
                return null;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        List<String> keys = new ArrayList<>(sKeys);
        List<CronTaskCords> values = new ArrayList<>();
        keys.forEach(key -> values.add(JSON.parseObject(stringRedisTemplate.opsForValue().get(key), CronTaskCords.class)));

        return values;
    }

    public List<String> get(String key){
        return stringRedisTemplate.opsForList().range(TALK_KEY+key, 0, -1);
    }

    public void set(String key, Object value){
        stringRedisTemplate.opsForList().rightPush(TALK_KEY+key, JSON.toJSONString(value));
    }
}
