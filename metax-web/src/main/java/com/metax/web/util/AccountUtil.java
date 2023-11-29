package com.metax.web.util;

import com.alibaba.fastjson2.JSON;
import com.metax.common.core.exception.ServiceException;
import com.metax.web.domain.ChannelAccount;
import com.metax.web.domain.weChat.WeChatServiceAccountConfig;
import com.metax.web.service.IChannelAccountService;
import me.chanjar.weixin.common.redis.RedisTemplateWxRedisOps;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.impl.WxMpRedisConfigImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.metax.common.core.constant.MetaxDataConstants.WECHAT_SERVICE_ACCOUNT;
import static com.metax.common.core.constant.WeChatConstants.WECHAT_SERVICE_ACCOUNT_ACCESS_TOKEN_PREFIX;

/**
 * @Author: hanabi
 */
@Configuration
public class AccountUtil {

    @Autowired
    private IChannelAccountService channelAccountService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 微信服务号账号集合
     */
    private ConcurrentMap<ChannelAccount, WxMpService> weChatServiceAccountMap = new ConcurrentHashMap<>();

    /**
     * 获取消息渠道配置信息
     *
     * @param id
     * @param clazz
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T getAccount(Long id, Class<T> clazz) {
        ChannelAccount channelAccount = channelAccountService.getById(id);
        if (Objects.isNull(channelAccount)){
            throw new ServiceException("发送账号为空");
        }
        if (clazz.equals(WxMpService.class)) {
            //如果请求的是微信服务号账号配置类 封装成WxMpService对象并存进账号集合
            return (T) ConcurrentHashMapUtils.computeIfAbsent(weChatServiceAccountMap, channelAccount, channelAccount1 -> initOfficialAccountService(JSON.parseObject(channelAccount.getAccountConfig(), WeChatServiceAccountConfig.class)));
        }
        return JSON.parseObject(channelAccount.getAccountConfig(), clazz);
    }

    /**
     * 初始化微信服务号
     * access_token 用redis存储
     * @param officialAccount
     * @return
     */
    public WxMpService initOfficialAccountService(WeChatServiceAccountConfig officialAccount) {
        WxMpService wxMpService = new WxMpServiceImpl();
        WxMpRedisConfigImpl config = new WxMpRedisConfigImpl(redisTemplateWxRedisOps(), WECHAT_SERVICE_ACCOUNT_ACCESS_TOKEN_PREFIX);
        config.setAppId(officialAccount.getAppId());
        config.setSecret(officialAccount.getSecret());
        config.setToken(officialAccount.getToken());
        config.useStableAccessToken(true);
        wxMpService.setWxMpConfigStorage(config);
        return wxMpService;
    }

    @Bean
    public RedisTemplateWxRedisOps redisTemplateWxRedisOps() {
        return new RedisTemplateWxRedisOps(stringRedisTemplate);
    }
}
