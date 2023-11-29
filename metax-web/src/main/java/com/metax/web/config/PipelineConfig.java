package com.metax.web.config;


import com.metax.web.domain.ProcessTemplate;
import com.metax.web.process.*;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * 业务流水线配置
 * @Author: hanabi
 */
@Configuration
public class PipelineConfig {

    @Autowired
    public PermissionVerificationProcess permissionVerificationProcess;
    @Autowired
    private PreCheckProcess preCheckProcess;
    @Autowired
    private VariableClassificationProcess variableClassificationProcess;
    @Autowired
    private ReceiverCheckProcess receiverCheckProcess;
    @Autowired
    private DataPlaceholderProcess dataPlaceholderProcess;
    @Autowired
    private SendMqProcess sendMqProcess;
    @Autowired
    private CronTaskDataProcess cronTaskDataProcess;
    @Autowired
    private TypeMappingProcess typeMappingProcess;


    /**
     * 消息发送责任链
     *
     * @return
     */
    @Bean("sendMessageTemplate")
    public ProcessTemplate sendMessageTemplate() {
        return ProcessTemplate.builder()
                //加入责任链
                .processes(Arrays.asList(
                        permissionVerificationProcess,preCheckProcess, variableClassificationProcess,
                        receiverCheckProcess, dataPlaceholderProcess,typeMappingProcess, sendMqProcess
                )).build();
    }

    /**
     * 定时任务处理责任链
     *
     * @return
     */
    @Bean("cronTaskTemplate")
    public ProcessTemplate cronTaskTemplate() {
        return ProcessTemplate.builder()
                .processes(Arrays.asList(
                        cronTaskDataProcess
                )).build();
    }

}
