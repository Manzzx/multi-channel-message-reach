package com.metax.web.domain.content;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;

/**
 * SendContent过渡对象
 * @Author: hanabi
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SendTaskParamContent extends ProcessContent {

    private Long messageTemplateId;

    /**
     * 渠道类型
     */
    private Integer sendChannel;

    /**
     * key：占位符数据 value：接受者集合
     */
    private Map<String, Set<String>> sendTaskParams;

    /**
     * 是否带有占位符数据 0.没有 其他.占位符数量
     */
    private Integer isExitVariables;

    /**
     * 发送方
     */
    private Long sender;


}
