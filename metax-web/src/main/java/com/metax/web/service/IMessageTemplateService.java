package com.metax.web.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.metax.common.core.web.domain.AjaxResult;
import com.metax.web.domain.MessageTemplate;
import com.metax.web.dto.MessageTemplateDto;

import java.util.List;

/**
 * 消息模板Service接口
 * 
 * @author hanabi
 * @date 2023-09-08
 */
public interface IMessageTemplateService extends IService<MessageTemplate>
{

    boolean add(MessageTemplate messageTemplate);

    boolean edit(MessageTemplate messageTemplate);

    boolean delete(Long[] ids);

    public IPage<MessageTemplate> selectMessageTemplateList(MessageTemplateDto messageTemplate, Long pageNum, Long pageSize);

    boolean updateAudit(Long id, Integer status);

    List<MessageTemplate> getMessages();

    public IPage<MessageTemplate> selectAuditMessageTemplateList(MessageTemplateDto messageTemplate, Long pageNum, Long pageSize);

}
