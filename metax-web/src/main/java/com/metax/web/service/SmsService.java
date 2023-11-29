package com.metax.web.service;

import com.metax.common.core.web.domain.AjaxResult;
import com.metax.web.dto.QuerySmsRecordDto;
import com.metax.web.vo.SmsRecordPage;

/**
 * @Author: hanabi
 * @DateTime: 2023/10/30 23:08
 **/
public interface SmsService {

    SmsRecordPage pushRecord(QuerySmsRecordDto param);

    SmsRecordPage pushRecentRecord(QuerySmsRecordDto params);

    boolean clear();
}
