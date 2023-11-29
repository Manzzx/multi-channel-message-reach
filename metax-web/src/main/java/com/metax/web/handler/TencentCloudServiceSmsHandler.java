package com.metax.web.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.metax.web.domain.SendTaskInfo;
import com.metax.web.domain.centent.TencentSmsConfig;
import com.metax.web.dto.content.SmsContentModel;
import com.metax.web.util.AccountUtil;
import com.metax.web.util.DataUtil;
import com.metax.web.util.tencent.TencentCloudSmsSendUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 腾讯云短信handler
 * https://cloud.tencent.com/document/product/382/43194#.E9.80.9A.E8.BF.87-maven-.E5.AE.89.E8.A3.85.EF.BC.88.E6.8E.A8.E8.8D.90.EF.BC.89
 * @Author: hanabi
 * @DateTime: 2023/10/31 16:34
 **/
@Service
@Slf4j
public class TencentCloudServiceSmsHandler extends ChannelHandler {

    @Autowired
    private AccountUtil accountUtil;
    @Autowired
    private DataUtil dataUtil;

    @Override
    void doHandler(SendTaskInfo sendTaskInfo) {
        try {
            TencentSmsConfig account = accountUtil.getAccount(sendTaskInfo.getMessageTemplate().getSendAccount(), TencentSmsConfig.class);
            String[] phones = buildPhone(sendTaskInfo);
            String[] params = buildParams(sendTaskInfo);
            String send = TencentCloudSmsSendUtils.send(account, params, phones);
            dataUtil.confirmSend(send, sendTaskInfo.getMessageId(), sendTaskInfo.getSendMessageKey(), sendTaskInfo.getSendTaskId(), new Exception());
        } catch (Exception e) {
            dataUtil.confirmSend(null, sendTaskInfo.getMessageId(), sendTaskInfo.getSendMessageKey(), sendTaskInfo.getSendTaskId(), e);
            log.error("腾讯云短信发送异常:" + e.getMessage());
        }


    }

    private String[] buildParams(SendTaskInfo sendTaskInfo) {
        SmsContentModel smsContentModel = JSON.parseObject(sendTaskInfo.getMessageTemplate().getMsgContent(), SmsContentModel.class);
        JSONObject jsonObject = getSmsContent(smsContentModel);
        String[] phones = new String[jsonObject.size()];
        int index = 0;
        for (String key : jsonObject.keySet()) {
            phones[index] = jsonObject.getString(key);
            ++index;
        }
        return phones;
    }

    private String[] buildPhone(SendTaskInfo sendTaskInfo) {
        List<String> list = new ArrayList<>();
        for (String receiver : sendTaskInfo.getReceivers()) {
            String phoneNumberWithPrefix = "+86" + receiver;
            list.add(phoneNumberWithPrefix);
        }
        return list.toArray(new String[list.size() - 1]);
    }
}
