package com.metax.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 接受短信回执查询参数
 * @Author: hanabi
 * @DateTime: 2023/10/30 23:18
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuerySmsRecordDto {

    private String pageNum;

    private String pageSize;

    private String account;

    private String phone;

    private String sendDate;

    private String serialId;

}
