package com.metax.web.dto.content;

import com.alibaba.fastjson2.annotation.JSONField;
import com.metax.web.domain.feishu.FeiShuRobotParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 飞书机器人消息体
 * @Author: hanabi
 * @DateTime: 2023/11/4 20:38
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeiShuRobotContentModel extends ContentModel{

    /**
     * msgType
     */
    private String msgType;

    /**
     * text
     */
    private Text text;


    /**
     * Text
     */
    @NoArgsConstructor
    @Data
    @Builder
    @AllArgsConstructor
    public static class Text {
        /**
         * content
         */
        private String content;
    }
}
