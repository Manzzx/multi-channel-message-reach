package com.metax.web.domain.weChat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 微信公众号账号配置信息
 * @Author: hanabi
 * @DateTime: 2023/10/26 9:41
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeChatServiceAccountConfig {

    /**
     * 账号相关
     */
    private String appId;

    private String secret;

    private String token;

}
