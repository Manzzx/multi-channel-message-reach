package com.metax.web.domain.content;

import com.metax.web.domain.MessageTemplate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 定时任务上下文
 * @Author: hanabi
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CronTaskContent extends ProcessContent{

    private MessageTemplate messageTemplate;

    /**
     * 发送方
     */
    private Long sender;
}
