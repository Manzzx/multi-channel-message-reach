package com.metax.web.service.impl;

import com.metax.common.core.constant.HttpStatus;
import com.metax.common.core.context.SecurityContextHolder;
import com.metax.common.core.web.domain.AjaxResult;

import com.metax.web.domain.MessageTemplate;
import com.metax.web.domain.content.ProcessContent;
import com.metax.web.domain.ProcessTemplate;
import com.metax.web.process.BusinessProcess;
import com.metax.web.service.IMessageTemplateService;
import com.metax.web.service.SendMessageService;
import com.metax.web.xxljob.service.XxlJobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.metax.common.core.constant.MetaxDataConstants.AUDIT_PASS;
import static com.metax.common.core.constant.MetaxDataConstants.PERMITTED_USE;

@Service
@Slf4j
public class SendMessageServiceImpl implements SendMessageService {

    @Autowired
    private ProcessTemplate sendMessageTemplate;
    @Autowired
    private XxlJobService xxlJobService;
    @Autowired
    public IMessageTemplateService messageTemplateService;

    /**
     * 发送任务入口
     * @param content
     * @return
     */
    @Override
    public AjaxResult send(ProcessContent content) {
        List<BusinessProcess> processes = sendMessageTemplate.getProcesses();
        for (BusinessProcess process : processes) {
            content = process.process(content);
            if (content.getIsNeedBreak()){
                //责任链需要中断
                break;
            }
        }
        return content.getResponse();
    }

    /**
     * 定时消息任务启动
     * @param id
     * @return
     */
    @Override
    public AjaxResult start(Long id) {
        //定时任务查看是否已经被其它用户使用
        MessageTemplate messageTemplate = messageTemplateService.getById(id);
        if (!PERMITTED_USE.equals(String.valueOf(messageTemplate.getCurrentId()))) {
            if (!SecurityContextHolder.getUserId().equals(messageTemplate.getCurrentId())) {
                return AjaxResult.error(HttpStatus.ERROR, "该模板已被其它用户使用");
            }
        }
        if (!AUDIT_PASS.equals(messageTemplate.getAuditStatus())) {
            return AjaxResult.error(HttpStatus.ERROR, "该模板尚未通过审核");
        }
        return xxlJobService.start(id);
    }

    /**
     * 定时消息任务停止
     * @param id
     * @return
     */
    @Override
    public AjaxResult stop(Long id) {
        //定时任务查看是否已经被其它用户使用
        MessageTemplate messageTemplate = messageTemplateService.getById(id);
        if (!PERMITTED_USE.equals(String.valueOf(messageTemplate.getCurrentId()))) {
            if (!SecurityContextHolder.getUserId().equals(messageTemplate.getCurrentId())) {
                return AjaxResult.error(HttpStatus.ERROR, "该模板已被其它用户使用");
            }
        }
        if (!AUDIT_PASS.equals(messageTemplate.getAuditStatus())) {
            return AjaxResult.error(HttpStatus.ERROR, "该模板尚未通过审核");
        }
        return xxlJobService.stop(id);
    }


}
