package com.metax.web.domain.push.getui;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 查询用户个推token参数
 * @Author: hanabi
 * @DateTime: 2023/10/28 18:31
 **/
@NoArgsConstructor
@Data
@Builder
@AllArgsConstructor
public class QueryTokenParamDTO {
    /**
     * sign
     */
    @JSONField(name = "sign")
    private String sign;
    /**
     * timestamp
     */
    @JSONField(name = "timestamp")
    private String timestamp;
    /**
     * appkey
     */
    @JSONField(name = "appkey")
    private String appKey;
}
