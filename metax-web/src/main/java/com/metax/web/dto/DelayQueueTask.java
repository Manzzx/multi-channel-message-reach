package com.metax.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 延迟队列任务
 *
 * @Author: hanabi
 * @DateTime: 2023/11/17 10:59
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DelayQueueTask {

    private String messageRedisKey;

    private Long sendTaskId;

    private Long messageId;
}
