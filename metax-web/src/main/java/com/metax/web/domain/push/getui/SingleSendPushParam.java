package com.metax.web.domain.push.getui;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * 个推cid单推发送请求参数
 *
 * @Author: hanabi
 * @DateTime: 2023/10/29 15:06
 **/
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SingleSendPushParam {

    /**
     * requestId
     */
    @JSONField(name = "request_id")
    private String requestId;
    /**
     * settings
     */
    @JSONField(name = "settings")
    private SettingsVO settings;
    /**
     * audience
     */
    @JSONField(name = "audience")
    private AudienceVO audience;
    /**
     * pushMessage
     */
    @JSONField(name = "push_message")
    private PushMessageVO pushMessage;

    /**
     * SettingsVO
     */
    @NoArgsConstructor
    @Data
    public static class SettingsVO {
        /**
         * ttl
         */
        @JSONField(name = "ttl")
        private Integer ttl;
    }

    /**
     * AudienceVO
     */
    @NoArgsConstructor
    @Data
    @AllArgsConstructor
    @Builder
    public static class AudienceVO {
        /**
         * cid
         */
        @JSONField(name = "cid")
        private Set<String> cid;
    }

    /**
     * PushMessageVO
     */
    @NoArgsConstructor
    @Data
    @AllArgsConstructor
    @Builder
    public static class PushMessageVO {
        /**
         * notification
         */
        @JSONField(name = "notification")
        private NotificationVO notification;

        /**
         * NotificationVO
         */
        @NoArgsConstructor
        @Data
        @AllArgsConstructor
        @Builder
        public static class NotificationVO {
            /**
             * title
             */
            @JSONField(name = "title")
            private String title;
            /**
             * body
             */
            @JSONField(name = "body")
            private String body;

            /**
             * channelLevel
             */
            @JSONField(name = "channel_level")
            private String channelLevel;

            /**
             * clickType
             */
            @JSONField(name = "click_type")
            private String clickType;
            /**
             * url
             */
            @JSONField(name = "url")
            private String url;
            /**
             * intent
             */
            @JSONField(name = "intent")
            private String intent;
            /**
             * click_type为payload/payload_custom时必填
             */
            @JSONField(name = "payload")
            private String payload;
        }
    }
}
