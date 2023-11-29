package com.metax.web.process;

import com.metax.web.domain.content.ProcessContent;

/**
 * 业务流程责任链
 *
 * @Author: hanabi
 */
public interface BusinessProcess {

    public ProcessContent process(ProcessContent context);

}
