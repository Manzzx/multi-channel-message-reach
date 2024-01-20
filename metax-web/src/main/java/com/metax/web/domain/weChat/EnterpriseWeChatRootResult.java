package com.metax.web.domain.weChat;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 企业微信机器人返回数据类型
 *
 * @Author: hanabi
 * @DateTime: 2024/1/19 20:13
 **/
@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class EnterpriseWeChatRootResult {

    /**
     * 0 为成功
     */
    @JSONField(name = "errcode")
    private Integer errCode;

    /**
     * 返回错误信息
     */
    @JSONField(name = "errmsg")
    private String errMsg;

    /**
     * 文件类型，分别有语音(voice)和普通文件(file)
     */
    @JSONField(name = "type")
    private String type;

    /**
     * 媒体文件上传后获取的唯一标识，3天内有效
     */
    @JSONField(name = "media_id")
    private String mediaId;

    /**
     * 媒体文件上传时间戳
     */
    @JSONField(name = "created_at")
    private String createdAt;
}

