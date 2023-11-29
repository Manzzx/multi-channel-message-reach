package com.metax.web.service;

import com.metax.common.core.web.page.TableDataInfo;
import com.metax.web.vo.ReceiverRecordsPage;
import com.metax.web.vo.SendTaskInfoVoPage;

/**
 * @Author: hanabi
 * @DateTime: 2023/10/4 14:05
 **/
public interface IDataService {

    SendTaskInfoVoPage getCurrentDayData(int pageNum, int pageSize, String sendMessageKey,Long userId);

    boolean clear(String date,Long userId);

    TableDataInfo cronTaskCordsList(int pageNum, int pageSize, String messageTemplateId);

    ReceiverRecordsPage getReceiverByDay(int pageNum, int pageSize,String receiver, String sendMessageKey);

    SendTaskInfoVoPage getUserCurrentDayData(int pageNum, int pageSize, String sendMessageKey, Long userId);
}
