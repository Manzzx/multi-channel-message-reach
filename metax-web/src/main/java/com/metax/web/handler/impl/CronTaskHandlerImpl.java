package com.metax.web.handler.impl;

import com.metax.common.core.exception.ServiceException;
import com.metax.common.core.web.domain.AjaxResult;
import com.metax.web.domain.MessageTemplate;
import com.metax.web.domain.ProcessTemplate;
import com.metax.web.domain.content.CronTaskContent;
import com.metax.web.domain.content.ProcessContent;
import com.metax.web.handler.CronTaskHandler;
import com.metax.web.process.BusinessProcess;
import com.metax.web.service.IMessageTemplateService;
import com.metax.web.service.SendMessageService;
import com.metax.web.util.DataUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


import static com.metax.common.core.constant.MetaxDataConstants.CRON_TASK_FAIL;
import static com.metax.common.core.web.domain.AjaxResult.MSG_TAG;

/**
 * 定时任务处理入口
 * @Author: hanabi
 */
@Service
public class CronTaskHandlerImpl implements CronTaskHandler {

    @Resource
    private ProcessTemplate cronTaskTemplate;
    @Autowired
    private IMessageTemplateService messageTemplateService;
    @Autowired
    private SendMessageService sendMessageService;
    @Autowired
    private DataUtil dataUtil;

    @Override
    public void Handler(Long id,Long sender) {
        MessageTemplate messageTemplate = messageTemplateService.getById(id);
        ProcessContent content = CronTaskContent.builder().messageTemplate(messageTemplate).sender(sender).build();
        for (BusinessProcess process : cronTaskTemplate.getProcesses()) {
            content = process.process(content);
            if (content.getIsNeedBreak()) {
                recordError(messageTemplate, content.getResponse(),sender);
                throw new ServiceException((String) content.getResponse().get(MSG_TAG));
            }
        }

        //进入消息发送流程
        AjaxResult result = sendMessageService.send(content);
        if (result.isError()) {
            recordError(messageTemplate, result,sender);
            throw new ServiceException((String) result.get(MSG_TAG));
        }
    }

    /**
     * 记录异常
     *
     * @param messageTemplate
     * @param result
     */
    private void recordError(MessageTemplate messageTemplate, AjaxResult result,Long sender) {
        //记录·报错信息
        dataUtil.recordCronTaskStatus(CRON_TASK_FAIL, messageTemplate.getId(),sender, (String) result.get(MSG_TAG));
    }
}
