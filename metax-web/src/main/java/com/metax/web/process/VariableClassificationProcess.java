package com.metax.web.process;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.metax.common.core.constant.HttpStatus;
import com.metax.common.core.constant.MetaxDataConstants;
import com.metax.common.core.web.domain.AjaxResult;
import com.metax.web.domain.content.ProcessContent;
import com.metax.web.domain.content.SendTaskParamContent;
import com.metax.web.dto.SendForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 对变量数据进行统一分类
 * @Author: hanabi
 */
@Service
@Slf4j
public class VariableClassificationProcess implements BusinessProcess {

    @Override
    public ProcessContent process(ProcessContent context) {
        if (!(context instanceof SendForm)){
            context.setIsNeedBreak(true);
            context.setResponse(AjaxResult.error(HttpStatus.ERROR,"发送流程上下文断裂"));
            return context;
        }

        SendForm sendForm = (SendForm) context;
        Map<String,Set<String>> sendTaskParams = new HashMap<>();
        List<String> list = StrUtil.split(sendForm.getReceivers(), MetaxDataConstants.SEPARATOR);
        //接受者去重
        List<String> receivers = list.stream().distinct().collect(Collectors.toList());

        if (sendForm.getIsExitVariables() !=0) {
            //处理占位符数据
            List<String> jsonObjects = JSONUtil.toList(sendForm.getVariables(), String.class);
            if (receivers.size() != jsonObjects.size()){
                //接受者和占位符数据数量不一致
                context.setIsNeedBreak(true);
                context.setResponse(AjaxResult.error(HttpStatus.ERROR,"接受者和占位符数据数量不一致"));
                return context;
            }
            mergeData(sendTaskParams, receivers, jsonObjects);
        }else {
            sendTaskParams.put("",new HashSet<>(receivers));
        }

        SendTaskParamContent sendTaskParamContext = SendTaskParamContent.builder()
                .sendTaskParams(sendTaskParams)
                .messageTemplateId(sendForm.getMessageTemplateId())
                .sendChannel(sendForm.getSendChannel())
                .isExitVariables(sendForm.getIsExitVariables())
                .sender(sendForm.getSender())
                .build();
        return sendTaskParamContext;
    }

    /**
     * 合并相同占位符数据
     * @param sendTaskParams
     * @param receivers
     * @param jsonObjects
     */
    private void mergeData(Map<String, Set<String>> sendTaskParams, List<String> receivers, List<String> jsonObjects) {
        for (int i = 0; i < receivers.size(); i++) {
            //如果不存在 新建接受者集合
            if(sendTaskParams.get(jsonObjects.get(i)) == null){
                HashSet<String> hashSet = new HashSet<>();
                hashSet.add(receivers.get(i));
                //以占位符数据为key
                sendTaskParams.put(jsonObjects.get(i),hashSet);
            }
            //如果已经存在 加入接受者集合
            sendTaskParams.get(jsonObjects.get(i)).add(receivers.get(i));
        }
    }
}
