package com.metax.web.util;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.metax.common.core.exception.ServiceException;
import com.metax.web.domain.SendTaskInfo;
import com.metax.web.domain.dingding.DingDingRobotConfig;
import com.metax.web.domain.dingding.DingDingRobotParam;
import com.metax.web.dto.content.DingDingRobotContentModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.regex.Pattern;

import static com.metax.common.core.constant.SendMessageTypeConstants.*;
import static com.metax.common.core.constant.MetaxDataConstants.PUSH_NOW;

/**
 * 钉钉自定义机器人发送服务工具类
 *
 * @Author: hanabi
 * @DateTime: 2023/10/1 16:18
 **/
@Slf4j
@Component
public class DingDingRobotUtils {

    private static final String regex = "^1[3456789]\\d{9}$"; // 手机号正则表达式

    public void send(DingDingRobotConfig config, SendTaskInfo sendTaskInfo) {
        DingTalkClient client = new DefaultDingTalkClient(createSignUrl(config));
        OapiRobotSendRequest request = new OapiRobotSendRequest();
        DingDingRobotContentModel contentModel = JSON.parseObject(sendTaskInfo.getMessageTemplate().getMsgContent(), DingDingRobotContentModel.class);
        OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
        //判断是否需要@群成员
        if (SEND_ALL.equals(CollUtil.getFirst(sendTaskInfo.getReceivers()))) {
            //@所有人
            at.setIsAtAll(true);
        } else if (Pattern.matches(regex, CollUtil.getFirst(sendTaskInfo.getReceivers()))) {
            //输入的是手机号
            at.setAtMobiles(new ArrayList<>(sendTaskInfo.getReceivers()));
        } else if (!PUSH_NOW.equals(CollUtil.getFirst(sendTaskInfo.getReceivers())) && !Pattern.matches(regex, CollUtil.getFirst(sendTaskInfo.getReceivers()))) {
            //接受者不为零且不是手机号 也就是钉钉号
            at.setAtUserIds(new ArrayList<>(sendTaskInfo.getReceivers()));
        }

        if (TEXT_NAME.equals(contentModel.getSendType())) {
            //文本类型
            JSONObject jsonObject = JSON.parseObject(contentModel.getContent());
            DingDingRobotParam.Text text = jsonObject.toJavaObject(DingDingRobotParam.Text.class);
            request.setText(JSON.toJSONString(text));
            request.setMsgtype("text");
        }
        if (LINK_NAME.equals(contentModel.getSendType())) {
            //link类型
            JSONObject jsonObject = JSON.parseObject(contentModel.getContent());
            DingDingRobotParam.Link link = jsonObject.toJavaObject(DingDingRobotParam.Link.class);
            request.setLink(JSON.toJSONString(link));
            request.setMsgtype("link");
        }
        if (MARKDOWN_NAME.equals(contentModel.getSendType())) {
            //markdown类型
            JSONObject jsonObject = JSON.parseObject(contentModel.getContent());
            DingDingRobotParam.Markdown markdown = jsonObject.toJavaObject(DingDingRobotParam.Markdown.class);
            request.setMarkdown(JSON.toJSONString(markdown));
            request.setMsgtype("markdown");
        }
        if (ACTION_CARD_NAME.equals(contentModel.getSendType())) {
            //actionCard类型
            JSONObject jsonObject = JSON.parseObject(contentModel.getContent());
            DingDingRobotParam.ActionCard actionCard = jsonObject.toJavaObject(DingDingRobotParam.ActionCard.class);
            request.setActionCard(JSON.toJSONString(actionCard));
            request.setMsgtype("actionCard");
        }
        if (FEED_CARD_NAME.equals(contentModel.getSendType())) {
            //feedCard类型
            JSONObject jsonObject = JSON.parseObject(contentModel.getContent());
            DingDingRobotParam.FeedCard feedCard = jsonObject.toJavaObject(DingDingRobotParam.FeedCard.class);
            request.setFeedCard(JSON.toJSONString(feedCard));
            request.setMsgtype("feedCard");
        }
        request.setAt(at);
        OapiRobotSendResponse response = null;
        try {
            response = client.execute(request);
            if (!response.isSuccess()){
                throw new ServiceException("响应码:"+response.getErrcode()+" 失败原因:"+response.getErrmsg());
            }
        } catch (Exception e) {
            throw new ServiceException("钉钉自定义机器人发送异常:"+e.getMessage());
        }
    }

    /**
     * 生成加签后的Webhook地址
     *
     * @return
     */
    public static String createSignUrl(DingDingRobotConfig config) {
        long timestamp = System.currentTimeMillis();
        String secret = config.getSecret();

        String sign = null;
        try {
            String stringToSign = timestamp + "\n" + secret;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
            sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)), "UTF-8");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return (config.getWebhook() + "&timestamp=" + timestamp + "&sign=" + sign);
    }
}
