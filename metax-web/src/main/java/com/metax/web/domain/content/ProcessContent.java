package com.metax.web.domain.content;

import com.metax.common.core.web.domain.AjaxResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 责任链上下文对象
 * @Author: hanabi
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcessContent {

    /**
     * 是否需要中断责任链
     */
    private Boolean isNeedBreak = false;

    /**
     * 处理结果
     */
    private AjaxResult response;



}
