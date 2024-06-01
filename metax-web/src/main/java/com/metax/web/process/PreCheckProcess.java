package com.metax.web.process;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.metax.common.core.constant.HttpStatus;
import com.metax.common.core.context.SecurityContextHolder;
import com.metax.common.core.web.domain.AjaxResult;
import com.metax.web.domain.content.ProcessContent;
import com.metax.web.dto.SendForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * 数据前置检查
 * @Author: hanabi
 */
@Service
@Slf4j
public class PreCheckProcess implements BusinessProcess {

    @Override
    public ProcessContent process(ProcessContent context) {
        if (!(context instanceof SendForm)){
            context.setIsNeedBreak(true);
            context.setResponse(AjaxResult.error(HttpStatus.ERROR,"发送流程上下文断裂"));
            return context;
        }

        SendForm sendForm = (SendForm) context;
        if (StrUtil.isBlank(sendForm.getReceivers())) {
            //中断
            sendForm.setIsNeedBreak(true);
            sendForm.setResponse(AjaxResult.error(HttpStatus.ERROR,"消息模板id:"+sendForm.getMessageTemplateId()+" 接受者为空"));
            return context;
        }

        if (sendForm.getMessageTemplateId() == null) {
            sendForm.setIsNeedBreak(true);
            sendForm.setResponse(AjaxResult.error(HttpStatus.ERROR,"模板id为空"));
            return context;
        }

        //如果有占位符但是未赋值数据
        if (sendForm.getIsExitVariables() != 0){
            if (StrUtil.isBlank(sendForm.getVariables())){
                sendForm.setIsNeedBreak(true);
                sendForm.setResponse(AjaxResult.error(HttpStatus.ERROR,"消息模板id:"+sendForm.getMessageTemplateId()+" 消息模板带有占位符 请赋值!"));
                return context;
            }

            //判断是否存在占位符数据为空的项
            List<JSONObject> jsonObjects = JSONUtil.toList(sendForm.getVariables(), JSONObject.class);
            for (JSONObject jsonObject : jsonObjects) {
                if (jsonObject.keySet().size() < sendForm.getIsExitVariables()){
                    //存在空项
                    context.setIsNeedBreak(true);
                    context.setResponse(AjaxResult.error(HttpStatus.ERROR,"消息模板id:"+sendForm.getMessageTemplateId()+" 存在占位符数据为空"));
                    return context;
                }
                for (String key : jsonObject.keySet()) {
                    Object value = jsonObject.get(key);
                    if (ObjectUtil.isEmpty(value)) {
                        //存在空项
                        context.setIsNeedBreak(true);
                        context.setResponse(AjaxResult.error(HttpStatus.ERROR,"消息模板id:"+sendForm.getMessageTemplateId()+" 存在占位符数据为空"));
                        return context;
                    }
                }
            }
        }

        //防止前端异常被赋值数据
        if (sendForm.getIsExitVariables() == 0){
            if (!StrUtil.isBlank(sendForm.getVariables())){
                sendForm.setIsNeedBreak(true);
                sendForm.setResponse(AjaxResult.error(HttpStatus.ERROR,"无需要赋值的占位符!"));
                return context;
            }
        }

        //如果发送方为空（实时发送）
        if (Objects.isNull(sendForm.getSender())){
            sendForm.setSender(SecurityContextHolder.getUserId());
        }

        return sendForm;
    }
}
