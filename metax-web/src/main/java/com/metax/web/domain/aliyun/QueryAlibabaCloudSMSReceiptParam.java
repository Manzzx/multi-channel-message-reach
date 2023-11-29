package com.metax.web.domain.aliyun;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 查询阿里云短信回执参数
 * @Author: hanabi
 * @DateTime: 2023/10/29 20:21
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryAlibabaCloudSMSReceiptParam {

    /**
     * 手机号 必选
     */
    private String phone;

    /**
     * 流水号 可选
     */
    private String bizId;


    /**
     * 发送日期 格式：yyyyMMdd 必选
     */
    private String sendDate;

    /**
     * 查询页大小 必选
     */
    private long pageSize;

    /**
     * 当前页数 必选
     */
    private long pageNum;
}
