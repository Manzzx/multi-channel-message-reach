package com.metax.web.domain.aliyun;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 阿里云短信配置类
 *
 * @Author: hanabi
 * @DateTime: 2023/9/26 12:30
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlibabaCloudSmsConfig {

    /**
     * regionId
     */
    private String regionId;

    /**
     * accessKeyId
     */
    private String accessKeyId;

    /**
     * accessSecret
     */
    private String accessSecret;

    /**
     * 模板code
     */
    private String templateCode;

    /**
     * 签名
     */
    private String signName;


    /**
     * 第三方服务名称
     */
    private String serviceName;

}
