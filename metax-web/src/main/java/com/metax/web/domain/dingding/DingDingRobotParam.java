package com.metax.web.domain.dingding;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 发送钉钉自定义机器人请求参数
 * https://open.dingtalk.com/document/group/custom-robot-access
 *
 * @Author: hanabi
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DingDingRobotParam {
    /**
     * at
     */
    private At at;
    /**
     * text
     */
    private Text text;
    /**
     * link
     */
    private Link link;
    /**
     * markdown
     */
    private Markdown markdown;
    /**
     * actionCard
     */
    private ActionCard actionCard;
    /**
     * feedCard
     */
    private FeedCard feedCard;
    /**
     * msgtype
     */
    private String msgtype;

    /**
     * At
     */
    @NoArgsConstructor
    @Data
    @Builder
    @AllArgsConstructor
    public static class At {
        /**
         * atMobiles
         */
        private List<String> atMobiles;
        /**
         * atUserIds
         */
        private List<String> atUserIds;
        /**
         * isAtAll
         */
        private Boolean isAtAll;
    }

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

    /**
     * Link
     */
    @NoArgsConstructor
    @Data
    @AllArgsConstructor
    @Builder
    public static class Link {
        /**
         * text
         */
        private String text;
        /**
         * title
         */
        private String title;
        /**
         * picUrl
         */
        private String picUrl;
        /**
         * messageUrl
         */
        private String messageUrl;
    }

    /**
     * Markdown
     */
    @NoArgsConstructor
    @Data
    @AllArgsConstructor
    @Builder
    public static class Markdown {
        /**
         * title
         */
        private String title;
        /**
         * text
         */
        private String text;
    }

    /**
     * ActionCard
     */
    @NoArgsConstructor
    @Data
    @AllArgsConstructor
    @Builder
    public static class ActionCard {
        /**
         * title
         */
        private String title;
        /**
         * text
         */
        private String text;
        /**
         * btnOrientation
         */
        private String btnOrientation;
        /**
         * btns
         */
        private List<Btns> btns;

        /**
         * Btns
         */
        @NoArgsConstructor
        @Data
        @AllArgsConstructor
        @Builder
        public static class Btns {
            /**
             * title
             */
            private String title;
            /**
             * actionURL
             */
            @JSONField(name = "actionURL")
            private String actionUrl;
        }
    }

    /**
     * FeedCard
     */
    @NoArgsConstructor
    @Data
    @AllArgsConstructor
    @Builder
    public static class FeedCard {
        /**
         * links
         */
        private List<Links> links;

        /**
         * Links
         */
        @NoArgsConstructor
        @Data
        @AllArgsConstructor
        @Builder
        public static class Links {
            /**
             * title
             */
            private String title;
            /**
             * messageURL
             */
            @JSONField(name = "messageURL")
            private String messageUrl;
            /**
             * picURL
             */
            @JSONField(name = "picURL")
            private String picUrl;
        }
    }
}
