package com.metax.web.handler;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.common.base.Throwables;
import com.metax.web.domain.ChannelAccount;
import com.metax.web.domain.SendTaskInfo;
import com.metax.web.service.IChannelAccountService;
import com.metax.web.util.DataUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.metax.common.core.constant.MetaxDataConstants.HANDLER_SUFFIX;
import static com.metax.common.core.constant.MetaxDataConstants.SMS_SERVICE_KEY;

/**
 * 短信服务统一处理 主要用于路由到具体哪个第三方服务
 * @Author: hanabi
 */
@Service
@Slf4j
public class SmsHandler extends ChannelHandler{

    @Autowired
    private Map<String,ChannelHandler> smsHandlers;
    @Autowired
    private IChannelAccountService channelAccountService;
    @Autowired
    private DataUtil dataUtil;


    @Override
    void doHandler(SendTaskInfo sendTaskInfo) {
        try {
            ChannelAccount channelAccount = channelAccountService.getById(sendTaskInfo.getMessageTemplate().getSendAccount());
            String accountConfig = channelAccount.getAccountConfig();
            JSONObject jsonObject = JSONUtil.toBean(accountConfig, JSONObject.class);
            //路由到具体哪个第三方短信服务handler
            smsHandlers.get(jsonObject.get(SMS_SERVICE_KEY)+HANDLER_SUFFIX).handler(sendTaskInfo);
        } catch (Exception e) {
            dataUtil.confirmSend(null, sendTaskInfo.getMessageId(), sendTaskInfo.getSendMessageKey(), sendTaskInfo.getSendTaskId(), e);
            log.error("短信服务异常:{}", Throwables.getStackTraceAsString(e));
        }
    }
}
