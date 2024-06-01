package com.metax.web.process;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.metax.common.core.constant.HttpStatus;
import com.metax.common.core.constant.MetaxDataConstants;
import com.metax.common.core.web.domain.AjaxResult;
import com.metax.web.config.ChannelConfig;
import com.metax.web.domain.*;
import com.metax.web.domain.content.ProcessContent;
import com.metax.web.domain.content.SendContent;
import com.metax.web.domain.content.SendTaskParamContent;
import com.metax.web.dto.content.SmsContentModel;
import com.metax.web.dto.content.WeChatServiceAccountContentModel;
import com.metax.web.service.IMessageTemplateService;
import com.metax.web.util.ContentHolderUtil;
import com.metax.web.util.RedisKeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import static com.metax.common.core.constant.MetaxDataConstants.SMS;
import static com.metax.common.core.constant.MetaxDataConstants.WECHAT_SERVICE_ACCOUNT;

/**
 * 为发送前进行数据填充
 *
 * @Author: hanabi
 */
@Service
@Slf4j
public class DataPlaceholderProcess implements BusinessProcess {

    @Autowired
    private IMessageTemplateService messageTemplateService;
    @Autowired
    private ChannelConfig channelConfig;
    @Autowired
    private ContentHolderUtil contentHolderUtil;
    @Autowired
    private RedisKeyUtil redisKeyUtil;

    @Override
    public ProcessContent process(ProcessContent context) {
        if (!(context instanceof SendTaskParamContent)) {
            context.setIsNeedBreak(true);
            context.setResponse(AjaxResult.error(HttpStatus.ERROR, "发送流程上下文断裂"));
            return context;
        }
        SendTaskParamContent sendTaskParamContext = (SendTaskParamContent) context;
        Long templateId = sendTaskParamContext.getMessageTemplateId();
        MessageTemplate messageTemplate = messageTemplateService.getById(templateId);
        //设置发送状态
        messageTemplate.setMsgStatus(MetaxDataConstants.MSG_SENDING);
        //生成本次发送任务id
        Long sendTaskId = redisKeyUtil.createSendTaskId();

        //无占位符发送 只有一个sendTask
        if (sendTaskParamContext.getIsExitVariables() == 0) {
            //生成发送任务redisKey
            String messageRedisKey = RedisKeyUtil.createMessageRedisKey(sendTaskParamContext.getSender());
            //需要特殊处理的渠道 因为有些渠道不需要自己提取替换占位符
            channelConfig.needSkid.forEach(channel -> {
                if (channel.equals(sendTaskParamContext.getSendChannel())) {
                    //无占位符 不用传参到第三方服务 直接发送
                    processingSpecialChannel(channel, messageTemplate);
                }
            });
            SendTaskInfo sendTask = SendTaskInfo.builder().messageTemplate(messageTemplate)
                    .receivers(sendTaskParamContext.getSendTaskParams().get(StrUtil.EMPTY))
                    .messageId(redisKeyUtil.createMessageId(messageTemplate.getId()))
                    .sendMessageKey(messageRedisKey)
                    .sendTaskId(sendTaskId)
                    .sendStartTime(LocalDateTime.now())
                    .build();

            return SendContent.builder().sendTasks(Collections.singletonList(sendTask))
                    .sendCode(MetaxDataConstants.SEND_CODE)
                    .sendTime(LocalDateTime.now())
                    .sendChannel(sendTaskParamContext.getSendChannel())
                    .sendTaskId(sendTaskId)
                    .sender(sendTaskParamContext.getSender())
                    .build();
        }

        //有占位符发送
        return buildSendContext(sendTaskParamContext, messageTemplate, sendTaskId);
    }

    /**
     * 构建含占位符数据SendContext
     *
     * @param sendTaskParamContext
     * @param messageTemplate
     * @return
     */
    private SendContent buildSendContext(SendTaskParamContent sendTaskParamContext, MessageTemplate messageTemplate, Long sendTaskId) {
        Map<String, Set<String>> sendTaskParams = sendTaskParamContext.getSendTaskParams();
        //生成发送任务redisKey
        String messageRedisKey = RedisKeyUtil.createMessageRedisKey(sendTaskParamContext.getSender());

        List<SendTaskInfo> sendTasks = new ArrayList<>();
        String content = messageTemplate.getMsgContent();
        Iterator<Map.Entry<String, Set<String>>> iterator = sendTaskParams.entrySet().iterator();
        //遍历sendTaskParams
        while (iterator.hasNext()) {
            Map.Entry<String, Set<String>> entry = iterator.next();
            //填充占位符
            MessageTemplate copyMessageTemplate = BeanUtil.copyProperties(messageTemplate, MessageTemplate.class);
            //需要特殊处理的渠道 因为有些渠道不需要自己提取替换占位符
            channelConfig.needSkid.forEach(channel -> {
                if (channel.equals(sendTaskParamContext.getSendChannel())) {
                    processingSpecialChannelWithVerification(channel, copyMessageTemplate, entry);
                }
            });

            if (!channelConfig.needSkid.contains(sendTaskParamContext.getSendChannel())) {
                String completeContent = contentHolderUtil.replacePlaceHolder(content, entry.getKey());
                copyMessageTemplate.setMsgContent(completeContent);
            }

            SendTaskInfo sendTask = SendTaskInfo.builder().receivers(entry.getValue()).messageTemplate(copyMessageTemplate)
                    .messageId(redisKeyUtil.createMessageId(copyMessageTemplate.getId()))
                    .sendMessageKey(messageRedisKey)
                    .sendTaskId(sendTaskId)
                    .sendStartTime(LocalDateTime.now())
                    .build();
            sendTasks.add(sendTask);
        }

        return SendContent.builder().sendCode(MetaxDataConstants.SEND_CODE)
                .sendTasks(sendTasks)
                .sendTime(LocalDateTime.now())
                .sendChannel(sendTaskParamContext.getSendChannel())
                .sendTaskId(sendTaskId)
                .sender(sendTaskParamContext.getSender())
                .build();
    }

    /**
     * 带占位符数据特殊处理渠道
     */
    public void processingSpecialChannelWithVerification(Integer channel, MessageTemplate copyMessageTemplate, Map.Entry<String, Set<String>> entry) {
        if (channel.equals(SMS)) {
            SmsContentModel smsContentModel = JSON.parseObject(copyMessageTemplate.getMsgContent(), SmsContentModel.class);
            smsContentModel.setContent(entry.getKey());
            copyMessageTemplate.setMsgContent(JSON.toJSONString(smsContentModel));
        }
        if (channel.equals(WECHAT_SERVICE_ACCOUNT)) {
            WeChatServiceAccountContentModel weChatServiceAccountContentModel = JSON.parseObject(copyMessageTemplate.getMsgContent(), WeChatServiceAccountContentModel.class);
            weChatServiceAccountContentModel.setContent(entry.getKey());
            copyMessageTemplate.setMsgContent(JSON.toJSONString(weChatServiceAccountContentModel));
        }
    }

    /**
     * 不带占位符数据特殊处理渠道
     */
    public void processingSpecialChannel(Integer channel, MessageTemplate messageTemplate) {
        if (channel.equals(SMS)) {
            messageTemplate.setMsgContent(StrUtil.EMPTY);
        }
        if (channel.equals(WECHAT_SERVICE_ACCOUNT)) {
            return;
        }
    }
}
