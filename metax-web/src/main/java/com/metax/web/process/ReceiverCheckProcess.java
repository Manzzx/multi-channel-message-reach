package com.metax.web.process;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ReUtil;
import com.metax.common.core.constant.HttpStatus;
import com.metax.common.core.constant.MetaxDataConstants;
import com.metax.common.core.web.domain.AjaxResult;
import com.metax.web.domain.content.ProcessContent;
import com.metax.web.domain.content.SendTaskParamContent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 接受者校验
 * @Author: hanabi
 */
@Service
@Slf4j
public class ReceiverCheckProcess implements BusinessProcess {

    /**
     * 邮件和手机号正则
     */
    public static final HashMap<Integer, String> CHANNEL_REGEX_EXP = new HashMap<>();
    public static final String PHONE_REGEX_EXP = "^((13[0-9])|(14[5,7,9])|(15[0-3,5-9])|(166)|(17[0-9])|(18[0-9])|(19[1,8,9]))\\d{8}$";
    public static final String EMAIL_REGEX_EXP = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    /**
     * 初始化每一个发送渠道的校验规则
     */
    static {
        CHANNEL_REGEX_EXP.put(MetaxDataConstants.EMAIL, EMAIL_REGEX_EXP);
        CHANNEL_REGEX_EXP.put(MetaxDataConstants.SMS, PHONE_REGEX_EXP);
    }

    @Override
    public ProcessContent process(ProcessContent context) {
        if (!(context instanceof SendTaskParamContent)) {
            context.setIsNeedBreak(true);
            context.setResponse(AjaxResult.error(HttpStatus.ERROR, "发送流程上下文断裂"));
            return context;
        }
        SendTaskParamContent sendTaskParamContext = (SendTaskParamContent) context;
        Integer sendChannel = sendTaskParamContext.getSendChannel();
        if (CHANNEL_REGEX_EXP.get(sendChannel) == null) {
            //该渠道无校验规则
            return context;
        }
        sendTaskParamContext.getSendTaskParams().forEach((s, receiver) -> {
            //只有前面没有校验出非法接受者才校验（防止覆盖）
            if (!context.getIsNeedBreak()) {
                check(receiver, CHANNEL_REGEX_EXP.get(sendChannel), context);
            }
        });
        return context;
    }

    /**
     * 校验方法
     *
     * @param receiver
     * @param regex
     */
    private void check(Set<String> receiver, String regex, ProcessContent context) {
        //非法接受者
        List<String> illegalReceiver = receiver.stream()
                .filter(r -> !ReUtil.isMatch(regex, r))
                .collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(illegalReceiver)) {
            //存在非法接受者
            context.setIsNeedBreak(true);
            context.setResponse(AjaxResult.error(HttpStatus.ERROR, "存在非法接受者:" + illegalReceiver.toString()));
        }
    }
}
