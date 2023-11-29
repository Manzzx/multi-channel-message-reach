package com.metax.web.domain.dingding;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 钉钉群自定义机器人账号配置信息
 * @Author: hanabi
 * @DateTime: 2023/10/1 16:27
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DingDingRobotConfig {

    /**
     * 密钥
     */
    private String secret;

    /**
     * 自定义群机器人中的 webhook
     */
    private String webhook;
}
