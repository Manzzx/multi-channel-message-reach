package com.metax.web.domain.weChat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 企业微信机器人账号配置
 *
 * @Author: hanabi
 * @DateTime: 2024/1/19 14:37
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EnterpriseWeChatRobotConfig {

    /**
     * 群机器人的 webhook
     */
    private String webhook;
}
