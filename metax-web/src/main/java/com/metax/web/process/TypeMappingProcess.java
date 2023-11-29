package com.metax.web.process;

import com.alibaba.fastjson2.JSON;
import com.metax.web.domain.SendTaskInfo;
import com.metax.web.domain.content.ProcessContent;
import com.metax.web.domain.content.SendContent;
import com.metax.web.dto.content.DingDingRobotContentModel;
import com.metax.web.util.DataUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.metax.common.core.constant.MetaxDataConstants.DING_DING_ROBOT;

/**
 * 对发送mq前需要映射转换的类型进行处理
 * @Author: hanabi
 * @DateTime: 2023/10/6 21:45
 **/
@Service
@Slf4j
public class TypeMappingProcess implements BusinessProcess{

    @Autowired
    private DataUtil dataUtil;

    @Override
    public ProcessContent process(ProcessContent context) {
        SendContent sendContext = (SendContent) context;
        for (SendTaskInfo sendTask : sendContext.getSendTasks()) {
            String msgContent = sendTask.getMessageTemplate().getMsgContent();
            if (sendTask.getMessageTemplate().getSendChannel().equals(DING_DING_ROBOT)){
                //钉钉消息类型
                DingDingRobotContentModel contentModel = JSON.parseObject(msgContent, DingDingRobotContentModel.class);
                contentModel.setSendType(dataUtil.sendTypeMapping.get(contentModel.getSendType()));
                sendTask.getMessageTemplate().setMsgContent(JSON.toJSONString(contentModel));
            }
        }

        return context;
    }
}
