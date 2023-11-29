package com.metax.web.service;

import com.metax.common.core.web.domain.AjaxResult;
import com.metax.web.domain.content.ProcessContent;

public interface SendMessageService {

    public AjaxResult send(ProcessContent sendForm);

    public AjaxResult start(Long id);

    public AjaxResult stop(Long id);
}
