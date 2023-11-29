package com.metax.web.dto.content;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 短信信息模型
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SmsContentModel extends ContentModel{

    /**
     * 文本
     */
    private String content;

    /**
     * 链接 主要用于长链转短链
     */
    private String url;
}
