package com.metax.web.handler;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.metax.web.domain.SendTaskInfo;
import com.metax.web.dto.content.WeChatServiceAccountContentModel;
import com.metax.web.util.AccountUtil;
import com.metax.web.util.DataUtil;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.metax.common.core.constant.MetaxDataConstants.SEPARATOR;
import static com.metax.common.core.constant.WeChatConstants.WECHAT_SERVICE_ACCOUNT_URL_NAME;

/**
 * 微信服务号handler
 * @Author: hanabi
 * @DateTime: 2023/10/25 21:52
 *
 **/
@Service
@Slf4j
public class WeChatServiceAccountHandler extends ChannelHandler{

    @Autowired
    private AccountUtil accountUtil;
    @Autowired
    private DataUtil dataUtil;

    @Override
    void doHandler(SendTaskInfo sendTaskInfo) {
        try {
            WeChatServiceAccountContentModel contentModel = JSON.parseObject(sendTaskInfo.getMessageTemplate().getMsgContent(), WeChatServiceAccountContentModel.class);
            WxMpService wxMpService = accountUtil.getAccount(sendTaskInfo.getMessageTemplate().getSendAccount(), WxMpService.class);
            List<WxMpTemplateMessage> wxMpTemplateMessages = assembleParameters(sendTaskInfo.getReceivers(), contentModel);
            StringBuilder msg = new StringBuilder();
            for (WxMpTemplateMessage message : wxMpTemplateMessages) {
                msg.append(wxMpService.getTemplateMsgService().sendTemplateMsg(message)).append(SEPARATOR);
            }
            msg.deleteCharAt(msg.lastIndexOf(SEPARATOR));
            dataUtil.confirmSend(msg.toString(), sendTaskInfo.getMessageId(), sendTaskInfo.getSendMessageKey(), sendTaskInfo.getSendTaskId(), new Exception());
        } catch (Exception e) {
            dataUtil.confirmSend(null, sendTaskInfo.getMessageId(), sendTaskInfo.getSendMessageKey(), sendTaskInfo.getSendTaskId(), e);
            log.error("微信服务号消息发送异常:" + e.getMessage());
        }

    }

    /**
     * 组装每一个模板消息参数
     * @return
     */
    public List<WxMpTemplateMessage> assembleParameters(Set<String> users,WeChatServiceAccountContentModel contentModel){
        Map<String,String> map = JSON.parseObject(contentModel.getContent(), Map.class);
        //提前将跳转链接取出来
        String url = map.get(WECHAT_SERVICE_ACCOUNT_URL_NAME);
        if (StrUtil.isBlank(url)){
            url=contentModel.getUrl();
        }else {
            map.remove(WECHAT_SERVICE_ACCOUNT_URL_NAME);
        }
        List<WxMpTemplateMessage> wxMpServices = new ArrayList<>(users.size());
        for (String user : users) {
            WxMpTemplateMessage message = WxMpTemplateMessage.builder()
                    .toUser(user)
                    .url(url)
                    .data(buildWxTemplateData(map))
                    .templateId(contentModel.getTemplateId())
                    .miniProgram(new WxMpTemplateMessage.MiniProgram(null, null, false))
                    .build();
            wxMpServices.add(message);
        }
        return wxMpServices;
    }

    /**
     * 构建模板消息里面的参数数据
     * @param map
     * @return
     */
    public List<WxMpTemplateData> buildWxTemplateData(Map<String,String> map){
        List<WxMpTemplateData> wxMpTemplateData = new ArrayList<>(map.size());
        map.forEach((k,v)->wxMpTemplateData.add(new WxMpTemplateData(k,v)));
        return wxMpTemplateData;
    }


}
