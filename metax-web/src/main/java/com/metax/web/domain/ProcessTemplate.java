package com.metax.web.domain;

import com.metax.web.process.BusinessProcess;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 串联处理器形成责任链
 * @Author: hanabi
 */
@Data
@Builder
public class ProcessTemplate {

    private List<BusinessProcess> processes;
}
