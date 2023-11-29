package com.metax.web.xxljob.service;

import com.metax.common.core.web.domain.AjaxResult;
import com.metax.web.domain.MessageTemplate;
import com.metax.web.xxljob.domain.XxlJobInfo;

public interface XxlJobService {

    public AjaxResult save(MessageTemplate messageTemplate);

    public XxlJobInfo buildXxlJobInfo(MessageTemplate messageTemplate);

    public AjaxResult start(Long id);

    public AjaxResult stop(Long id);

    public AjaxResult update(MessageTemplate messageTemplate);

    public AjaxResult remove(Integer id);
}
