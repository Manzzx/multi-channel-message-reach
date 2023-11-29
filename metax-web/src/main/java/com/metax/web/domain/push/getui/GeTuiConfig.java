package com.metax.web.domain.push.getui;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户个推账号配置
 * @Author: hanabi
 * @DateTime: 2023/10/29 0:22
 **/
@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class GeTuiConfig {

    private String appId;

    private String appKey;

    private String masterSecret;
}
