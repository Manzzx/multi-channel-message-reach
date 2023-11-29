package com.metax.web.vo;

import com.metax.web.domain.SendTaskInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 接受者维度消息下发记录
 * @Author: hanabi
 * @DateTime: 2023/10/19 9:01
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReceiverRecords {

    /**
     * 接受者
     */
    private String receiver;


    /**
     * 详情
     */
    private SendTaskInfoVo sendTask;


}
