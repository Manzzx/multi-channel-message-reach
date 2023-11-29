package com.metax.web.handler;

import com.metax.web.domain.ChannelAccount;
import com.metax.web.domain.SendTaskInfo;
import com.metax.web.domain.dingding.DingDingRobotConfig;
import com.metax.web.service.IChannelAccountService;
import com.metax.web.util.AccountUtil;
import com.metax.web.util.DataUtil;
import com.metax.web.util.DingDingRobotUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 *
 * 钉钉机器人handler
 * @Author: hanabi
 * @DateTime: 2023/9/30 11:59
 **/
@Service
@Slf4j
public class DingDingRobotHandler extends ChannelHandler {

    @Autowired
    private AccountUtil accountUtil;
    @Autowired
    private DataUtil dataUtil;
    @Autowired
    private DingDingRobotUtils dingDingRobotUtils;

    @Override
    void doHandler(SendTaskInfo sendTaskInfo) {
        DingDingRobotConfig account = accountUtil.getAccount(sendTaskInfo.getMessageTemplate().getSendAccount(), DingDingRobotConfig.class);

        try {
            dingDingRobotUtils.send(account, sendTaskInfo);
            dataUtil.confirmSend("钉钉群自定义机器人发送成功", sendTaskInfo.getMessageId(), sendTaskInfo.getSendMessageKey(), sendTaskInfo.sendTaskId, new Exception());
        } catch (Exception e) {
            dataUtil.confirmSend(null, sendTaskInfo.getMessageId(), sendTaskInfo.getSendMessageKey(), sendTaskInfo.getSendTaskId(), e);
            log.error("钉钉群自定义机器人发送异常:" + e.getMessage());
        }
    }
}
