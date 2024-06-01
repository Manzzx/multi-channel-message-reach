package com.metax.web.service;

import com.metax.web.dto.QuerySmsRecordDto;
import com.metax.web.vo.SmsRecordPage;

/**
 * @Author: hanabi
 * @DateTime: 2023/10/30 23:08
 **/
public interface SmsService {

    SmsRecordPage pullRecord(QuerySmsRecordDto param);

    SmsRecordPage pullRecentRecord(QuerySmsRecordDto params);

    boolean clear();
}
