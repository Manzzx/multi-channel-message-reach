package com.metax.web.domain.centent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 查询腾讯云短信回执参数
 * @Author: hanabi
 * @DateTime: 2023/10/29 20:21
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryTencentCloudSMSReceiptParam {

    /**
     * 手机号 必选
     */
    private String phone;

    /**
     * 开始时间 时间戳 精确到秒 必选
     */
    private Long beginTime;

    /**
     * 查询页开始位置 必选
     */
    private Long offset;

    /**
     * 条数 必选
     */
    private Long limit;
}
