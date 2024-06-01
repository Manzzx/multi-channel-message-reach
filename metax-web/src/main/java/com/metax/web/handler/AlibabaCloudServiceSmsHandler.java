package com.metax.web.handler;

import com.alibaba.fastjson2.JSON;
import com.google.common.base.Throwables;
import com.metax.web.domain.SendTaskInfo;
import com.metax.web.domain.aliyun.AlibabaCloudSmsConfig;
import com.metax.web.dto.content.SmsContentModel;
import com.metax.web.util.AccountUtil;
import com.metax.web.util.aliyun.AlibabaCloudSMSSendUtils;
import com.metax.web.util.DataUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

import static com.metax.common.core.constant.MetaxDataConstants.SEPARATOR;

/**
 * 阿里云短信服务handler
 *
 * @Author: hanabi
 * @DateTime: 2023/9/26 11:55
 **/
@Service
@Slf4j
public class AlibabaCloudServiceSmsHandler extends ChannelHandler {

    @Autowired
    private AccountUtil accountUtil;
    @Autowired
    private DataUtil dataUtil;

    @Override
    void doHandler(SendTaskInfo sendTaskInfo) {

        try {
            AlibabaCloudSmsConfig account = accountUtil.getAccount(sendTaskInfo.getMessageTemplate().getSendAccount(), AlibabaCloudSmsConfig.class);
            Set<String> receivers = sendTaskInfo.getReceivers();
            String phones = String.join(SEPARATOR, receivers);

            SmsContentModel smsContentModel = JSON.parseObject(sendTaskInfo.getMessageTemplate().getMsgContent(), SmsContentModel.class);
            String bzid = AlibabaCloudSMSSendUtils.sendMessage(account, phones, getSmsContent(smsContentModel).toJSONString());
            dataUtil.confirmSend(bzid, sendTaskInfo.getMessageId(), sendTaskInfo.getSendMessageKey(), sendTaskInfo.getSendTaskId(), new Exception());
        } catch (Exception e) {
            dataUtil.confirmSend(null, sendTaskInfo.getMessageId(), sendTaskInfo.getSendMessageKey(), sendTaskInfo.getSendTaskId(), e);
            log.error("阿里云短信发送异常:{}" , Throwables.getStackTraceAsString(e));
        }

    }
}
