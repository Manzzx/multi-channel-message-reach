package com.metax.web.process;

import com.metax.common.core.constant.HttpStatus;
import com.metax.common.core.web.domain.AjaxResult;
import com.metax.web.domain.MessageTemplate;
import com.metax.web.domain.content.ProcessContent;
import com.metax.web.service.IMessageTemplateService;
import com.metax.web.dto.SendForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.metax.common.core.constant.MetaxDataConstants.*;

/**
 * 消息发送权限校验
 *
 * @Author: hanabi
 * @DateTime: 2023/10/18 14:10
 **/
@Component
@Slf4j
public class PermissionVerificationProcess implements BusinessProcess {

    @Autowired
    public IMessageTemplateService messageTemplateService;

    @Override
    public ProcessContent process(ProcessContent context) {
        SendForm sendForm = (SendForm) context;
        MessageTemplate messageTemplate = messageTemplateService.getById(sendForm.getMessageTemplateId());

        if (!AUDIT_PASS.equals(messageTemplate.getAuditStatus())) {
            context.setIsNeedBreak(true);
            context.setResponse(AjaxResult.error(HttpStatus.ERROR, "该模板尚未通过审核"));
            return context;
        }

        return context;
    }
}
