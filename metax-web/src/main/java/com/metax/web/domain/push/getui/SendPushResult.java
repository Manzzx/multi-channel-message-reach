package com.metax.web.domain.push.getui;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 个推响应对象
 *
 * @Author: hanabi
 * @DateTime: 2023/10/29 14:45
 **/
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendPushResult {
    /**
     * msg
     */
    @JSONField(name = "msg")
    private String msg;
    /**
     * code
     */
    @JSONField(name = "code")
    private Integer code;
    /**
     * data
     */
    @JSONField(name = "data")
    private JSONObject data;

}
