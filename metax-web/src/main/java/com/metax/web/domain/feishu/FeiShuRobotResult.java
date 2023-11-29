package com.metax.web.domain.feishu;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 飞书机器人请求响应对象
 * @Author: hanabi
 * @DateTime: 2023/11/4 21:09
 **/
@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class FeiShuRobotResult {

    /**
     * extra
     */
    @JSONField(name = "Extra")
    private Object extra;
    /**
     * statusCode
     */
    @JSONField(name = "StatusCode")
    private Integer statusCode;
    /**
     * statusMessage
     */
    @JSONField(name = "StatusMessage")
    private String statusMessage;
    /**
     * code
     */
    @JSONField(name = "code")
    private Integer code;
    /**
     * msg
     */
    @JSONField(name = "msg")
    private String msg;
    /**
     * data
     */
    @JSONField(name = "data")
    private DataDTO data;

    /**
     * DataDTO
     */
    @NoArgsConstructor
    @Data
    public static class DataDTO {
    }
}