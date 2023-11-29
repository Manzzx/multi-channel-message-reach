package com.metax.web.domain.feishu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 飞书机器人账号配置信息
 * @Author: hanabi
 * @DateTime: 2023/11/4 20:56
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeiShuRobotConfig {

    /**
     * 自定义群机器人中的 webhook
     */
    private String webhook;
}
