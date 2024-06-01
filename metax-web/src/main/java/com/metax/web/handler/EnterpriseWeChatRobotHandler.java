package com.metax.web.handler;

import com.google.common.base.Throwables;
import com.metax.web.domain.SendTaskInfo;
import com.metax.web.domain.weChat.EnterpriseWeChatRobotConfig;
import com.metax.web.util.AccountUtil;
import com.metax.web.util.DataUtil;
import com.metax.web.util.tencent.EnterpriseWeChatRobotUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 企业微信机器人handler
 *
 * @Author: hanabi
 * @DateTime: 2024/1/19 15:23
 **/
@Service
@Slf4j
public class EnterpriseWeChatRobotHandler extends ChannelHandler {

    @Autowired
    private AccountUtil accountUtil;
    @Autowired
    private DataUtil dataUtil;

    @Override
    void doHandler(SendTaskInfo sendTaskInfo) {
        try {
            EnterpriseWeChatRobotConfig account = accountUtil.getAccount(sendTaskInfo.getMessageTemplate().getSendAccount(), EnterpriseWeChatRobotConfig.class);
            String result = EnterpriseWeChatRobotUtils.send(account, sendTaskInfo);
            dataUtil.confirmSend(result, sendTaskInfo.getMessageId(), sendTaskInfo.getSendMessageKey(), sendTaskInfo.sendTaskId, new Exception());
        } catch (Exception e) {
            dataUtil.confirmSend(null, sendTaskInfo.getMessageId(), sendTaskInfo.getSendMessageKey(), sendTaskInfo.getSendTaskId(), e);
            log.error("企业微信机器人发送异常:{}" , Throwables.getStackTraceAsString(e));
        }

    }
}


