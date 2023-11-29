package com.metax.web.process;

import com.metax.common.core.constant.HttpStatus;
import com.metax.common.core.web.domain.AjaxResult;
import com.metax.web.domain.MessageTemplate;
import com.metax.web.domain.content.CronTaskContent;
import com.metax.web.domain.content.ProcessContent;
import com.metax.web.util.CsvFileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 对定时任务数据进行处理
 * @Author: hanabi
 */
@Service
@Slf4j
public class CronTaskDataProcess implements BusinessProcess{

    @Autowired
    private CsvFileUtil csvFileUtil;

    @Override
    public ProcessContent process(ProcessContent context) {
        if (!(context instanceof CronTaskContent)){
            context.setIsNeedBreak(true);
            context.setResponse(AjaxResult.error(HttpStatus.ERROR,"定时任务处理流程上下文断裂"));
            return context;
        }
        CronTaskContent cronTaskContent = (CronTaskContent) context;
        MessageTemplate messageTemplate = cronTaskContent.getMessageTemplate();
        try {
            context = csvFileUtil.readCsvBuildSendFom(messageTemplate,cronTaskContent.getSender());
        } catch (Exception e) {
            context.setIsNeedBreak(true);
            context.setResponse(AjaxResult.error(e.getMessage()));
            e.printStackTrace();
        }
        return context;
    }
}
