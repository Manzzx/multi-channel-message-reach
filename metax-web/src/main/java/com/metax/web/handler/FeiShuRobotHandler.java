package com.metax.web.handler;

import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.http.HttpException;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson2.JSON;
import com.metax.common.core.constant.FeiShuRobotConstants;
import com.metax.common.core.exception.ServiceException;
import com.metax.web.domain.SendTaskInfo;
import com.metax.web.domain.feishu.FeiShuRobotConfig;
import com.metax.web.domain.feishu.FeiShuRobotParam;
import com.metax.web.domain.feishu.FeiShuRobotResult;
import com.metax.web.dto.content.FeiShuRobotContentModel;
import com.metax.web.util.AccountUtil;
import com.metax.web.util.DataUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.metax.common.core.constant.DdingDingSendMessageTypeConstants.TEXT;

/**
 * 飞书机器人handler
 *
 * @Author: hanabi
 * @DateTime: 2023/11/4 20:02
 **/
@Service
@Slf4j
public class FeiShuRobotHandler extends ChannelHandler {

    @Autowired
    private AccountUtil accountUtil;
    @Autowired
    private DataUtil dataUtil;

    @Override
    void doHandler(SendTaskInfo sendTaskInfo) {
        try {
            FeiShuRobotConfig account = accountUtil.getAccount(sendTaskInfo.getMessageTemplate().getSendAccount(), FeiShuRobotConfig.class);
            FeiShuRobotContentModel contentModel = JSON.parseObject(sendTaskInfo.getMessageTemplate().getMsgContent(), FeiShuRobotContentModel.class);
            FeiShuRobotParam feiShuRobotParam = buildFeiShuRobotParam(contentModel);
            String result = HttpRequest.post(account.getWebhook())
                    .header(Header.CONTENT_TYPE.getValue(), ContentType.JSON.getValue())
                    .body(JSON.toJSONString(feiShuRobotParam))
                    .timeout(2000)
                    .execute().body();
            FeiShuRobotResult feiShuRobotResult = JSON.parseObject(result, FeiShuRobotResult.class);

            if (feiShuRobotResult.getCode() != 0) {
                dataUtil.confirmSend(null, sendTaskInfo.getMessageId(), sendTaskInfo.getSendMessageKey(), sendTaskInfo.getSendTaskId(), new ServiceException(result));
                return;
            }
            dataUtil.confirmSend(result, sendTaskInfo.getMessageId(), sendTaskInfo.getSendMessageKey(), sendTaskInfo.sendTaskId, new Exception());
        } catch (Exception e) {
            dataUtil.confirmSend(null, sendTaskInfo.getMessageId(), sendTaskInfo.getSendMessageKey(), sendTaskInfo.getSendTaskId(), e);
            log.error("飞书自定义机器人发送异常:" + e.getMessage());
        }


    }

    /**
     * 构建飞书机器人发送请求参数
     *
     * @param contentModel
     * @return
     */
    public FeiShuRobotParam buildFeiShuRobotParam(FeiShuRobotContentModel contentModel) {
        FeiShuRobotParam.ContentDTO content = null;
        String msgType=null;
        if (FeiShuRobotConstants.TEXT.equals(contentModel.getMsgType())) {
            content = FeiShuRobotParam.ContentDTO.builder().text(contentModel.getText().getContent()).build();
            msgType = "text";
        }
        return FeiShuRobotParam.builder().content(content).msgType(msgType).build();
    }
}
