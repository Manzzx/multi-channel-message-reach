package com.metax.web.domain.weChat;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 企业微信机器人请求参数
 * https://developer.work.weixin.qq.com/document/path/91770
 *
 * @Author: hanabi
 * @DateTime: 2024/1/19 15:12
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EnterpriseWeChatRobotParam {

    /**
     * 消息类型msgtype
     */
    @JSONField(name = "msgtype")
    private String msgType;

    /**
     * text
     */
    @JSONField(name = "text")
    private TextDTO text;

    /**
     * markdown
     */
    @JSONField(name = "markdown")
    private MarkdownDTO markdown;

    /**
     * 图片
     */
    @JSONField(name = "image")
    private ImageDTO image;

    /**
     * 图文news
     */
    @JSONField(name = "news")
    private NewsDTO news;

    /**
     * file
     */
    @JSONField(name = "file")
    private FileDTO file;

    /**
     * voice
     */
    @JSONField(name = "voice")
    private VoiceDTO voice;


    /**
     * TextDTO
     */
    @NoArgsConstructor
    @Data
    @Builder
    @AllArgsConstructor
    public static class TextDTO {
        /**
         * content
         */
        @JSONField(name = "content")
        private String content;
        /**
         * mentionedList
         */
        @JSONField(name = "mentioned_list")
        private List<String> mentionedList;
        /**
         * mentionedMobileList
         */
        @JSONField(name = "mentioned_mobile_list")
        private List<String> mentionedMobileList;
    }

    /**
     * MarkdownDTO
     */
    @NoArgsConstructor
    @Data
    @Builder
    @AllArgsConstructor
    public static class MarkdownDTO {
        /**
         * content
         */
        @JSONField(name = "content")
        private String content;
    }

    /**
     * ImageDTO
     */
    @NoArgsConstructor
    @Data
    @Builder
    @AllArgsConstructor
    public static class ImageDTO {
        /**
         * 图片内容的base64编码
         */
        @JSONField(name = "base64")
        private String base64;
        /**
         * 图片内容（base64编码前）的md5值
         */
        @JSONField(name = "md5")
        private String md5;
    }

    /**
     * NewsDTO
     */
    @NoArgsConstructor
    @Data
    @Builder
    @AllArgsConstructor
    public static class NewsDTO {
        /**
         * articles
         */
        @JSONField(name = "articles")
        private List<ArticlesDTO> articles;

        /**
         * ArticlesDTO
         */
        @NoArgsConstructor
        @Data
        @Builder
        @AllArgsConstructor
        public static class ArticlesDTO {
            /**
             * title
             */
            @JSONField(name = "title")
            private String title;
            /**
             * description
             */
            @JSONField(name = "description")
            private String description;
            /**
             * url
             */
            @JSONField(name = "url")
            private String url;
            /**
             * picurl
             */
            @JSONField(name = "picurl")
            private String picurl;
        }
    }

    /**
     * FileDTO
     */
    @NoArgsConstructor
    @Data
    @Builder
    @AllArgsConstructor
    public static class FileDTO {
        /**
         * mediaId
         */
        @JSONField(name = "media_id")
        private String media_id;
    }

    /**
     * VoiceDTO
     */
    @NoArgsConstructor
    @Data
    @Builder
    @AllArgsConstructor
    public static class VoiceDTO {
        /**
         * mediaId
         */
        @JSONField(name = "media_id")
        private String media_id;
    }

}
