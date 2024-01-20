package com.metax.web.config;

import cn.hutool.core.util.StrUtil;
import com.metax.web.dto.content.ContentModel;
import com.metax.web.dto.content.SmsContentModel;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.metax.common.core.constant.MetaxDataConstants.*;
import static com.metax.common.core.constant.MetaxDataConstants.SEPARATOR;
import static com.metax.common.core.constant.QueueConstants.*;

/**
 * @Author: hanabi
 * @DateTime: 2023/9/29 16:32
 **/
@Configuration
public class ChannelConfig {

    /**
     * 缓存已支持的渠道
     */
    public List<Integer> channels = null;
    /**
     * 缓存已支持的渠道名称 每一个渠道的handler名称都是xxHandler
     */
    public List<String> channelNames = null;
    /**
     * 缓存已支持的渠道中文名称 每一个渠道的handler名称都是xxHandler
     */
    public List<String> channelCNNames = null;
    /**
     * 需要跳过占位符替换的渠道
     */
    public List<Integer> needSkid = null;
    /**
     * 各渠道过期时间
     */
    public Map<Integer,String> channelExpTime = new HashMap<>();



    @PostConstruct
    void init() {
        channelNames = StrUtil.split(SUPPORT_CHANNEL_NAME, SEPARATOR);
        channelCNNames = StrUtil.split(SUPPORT_CHANNEL_CN_NAME, SEPARATOR);
        channels = StrUtil.split(SUPPORT_CHANNEL, SEPARATOR)
                .stream().map(Integer::valueOf).collect(Collectors.toList());
        needSkid = Arrays.asList(SMS,WECHAT_SERVICE_ACCOUNT);

        channelExpTime.put(EMAIL,EMAIL_EXPIRATION_TIME);
        channelExpTime.put(SMS,SMS_EXPIRATION_TIME);
        channelExpTime.put(DING_DING_ROBOT,DING_DING_ROBOT_EXPIRATION_TIME);
        channelExpTime.put(WECHAT_SERVICE_ACCOUNT,WECHAT_SERVICE_ACCOUNT_EXPIRATION_TIME);
        channelExpTime.put(PUSH,PUSH_EXPIRATION_TIME);
        channelExpTime.put(FEI_SHU_ROBOT,FEI_SHU_ROBOT_EXPIRATION_TIME);
        channelExpTime.put(ENTERPRISE_WECHAT_ROBOT,ENTERPRISE_WECHAT_ROBOT_TIME);

    }

}
