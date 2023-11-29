package com.metax.web.domain.centent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 腾讯云短信配置类
 * @Author: hanabi
 * @DateTime: 2023/10/31 17:16
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TencentSmsConfig {

    /**
     * 指定接入地域域名，默认就近地域接入域名为 sms.tencentcloudapi.com
     * 也支持指定地域域名访问，例如广州地域的域名为 sms.ap-guangzhou.tencentcloudapi.com
     */
    private String endpoint;

    /**
     * 地域信息，可以直接填写字符串ap-guangzhou
     * 支持的地域列表参考 https://cloud.tencent.com/document/api/382/52071#.E5.9C.B0.E5.9F.9F.E5.88.97.E8.A1.A8
     */
    private String region;

    /**
     * SecretId 查询: https://console.cloud.tencent.com/cam/capi
     */
    private String secretId;

    /**
     * SecretKey 查询: https://console.cloud.tencent.com/cam/capi
     */
    private String secretKey;

    /**
     * 短信应用ID: 短信SdkAppId在 [短信控制台] 添加应用后生成的实际SdkAppId
     */
    private String smsSdkAppId;

    /**
     * 模板参数: 模板参数的个数需要与 TemplateId 对应模板的变量个数保持一致，若无模板参数，则设置为空
     */
    private String templateId;

    /**
     * 短信签名内容: 使用 UTF-8 编码，必须填写已审核通过的签名
     */
    private String signName;

    /**
     * 第三方服务名称
     */
    private String serviceName;
}
