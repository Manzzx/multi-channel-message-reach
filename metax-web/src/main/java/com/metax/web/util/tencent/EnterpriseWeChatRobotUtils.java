package com.metax.web.util.tencent;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.metax.common.core.constant.WeChatConstants;
import com.metax.common.core.exception.ServiceException;
import com.metax.web.domain.SendTaskInfo;
import com.metax.web.domain.weChat.EnterpriseWeChatRobotConfig;
import com.metax.web.domain.weChat.EnterpriseWeChatRobotParam;
import com.metax.web.domain.weChat.EnterpriseWeChatRootResult;
import com.metax.web.dto.content.EnterpriseWeChatRobotContentModel;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.regex.Pattern;

import static com.metax.common.core.constant.MetaxDataConstants.PUSH_NOW;

/**
 * 企业微信机器人工具类
 *
 * @Author: hanabi
 * @DateTime: 2024/1/20 11:09
 **/
@Slf4j
public class EnterpriseWeChatRobotUtils {

    private static final String regex = "^1[3456789]\\d{9}$"; // 手机号正则表达式

    public static String send(EnterpriseWeChatRobotConfig config, SendTaskInfo sendTaskInfo) {
        EnterpriseWeChatRobotContentModel contentModel = JSON.parseObject(sendTaskInfo.getMessageTemplate().getMsgContent(), EnterpriseWeChatRobotContentModel.class);
        EnterpriseWeChatRobotParam robotParam = buildEnterpriseWeChatRobotParam(sendTaskInfo, contentModel);
        String result = HttpRequest.post(config.getWebhook())
                .header(Header.CONTENT_TYPE.getValue(), ContentType.JSON.getValue())
                .body(JSON.toJSONString(robotParam))
                .timeout(2000)
                .execute().body();
        EnterpriseWeChatRootResult rootResult = JSON.parseObject(result, EnterpriseWeChatRootResult.class);
        if (rootResult.getErrCode() != 0) {
            throw new ServiceException(rootResult.toString());
        }
        return rootResult.toString();
    }

    private static EnterpriseWeChatRobotParam buildEnterpriseWeChatRobotParam(SendTaskInfo sendTaskInfo, EnterpriseWeChatRobotContentModel contentModel) {
        EnterpriseWeChatRobotParam robotParam = EnterpriseWeChatRobotParam.builder().build();
        JSONObject jsonObject = JSON.parseObject(contentModel.getContent());
        if (WeChatConstants.TEXT.equals(contentModel.getSendType())) {
            EnterpriseWeChatRobotParam.TextDTO textDTO = jsonObject.toJavaObject(EnterpriseWeChatRobotParam.TextDTO.class);
            robotParam.setMsgType(WeChatConstants.TEXT_NAME);
            if (Pattern.matches(regex, CollUtil.getFirst(sendTaskInfo.getReceivers()))) {
                //手机号列表
                textDTO.setMentionedMobileList(new ArrayList<>(sendTaskInfo.getReceivers()));
            } else {
                //userid列表
                textDTO.setMentionedList(new ArrayList<>(sendTaskInfo.getReceivers()));
            }
            robotParam.setText(textDTO);
        }
        if (WeChatConstants.MARKDOWN.equals(contentModel.getSendType())) {
            EnterpriseWeChatRobotParam.MarkdownDTO markdownDTO = jsonObject.toJavaObject(EnterpriseWeChatRobotParam.MarkdownDTO.class);
            robotParam.setMsgType(WeChatConstants.MARKDOWN_NAME);
            robotParam.setMarkdown(markdownDTO);
        }
        if (WeChatConstants.IMAGE.equals(contentModel.getSendType())) {
            EnterpriseWeChatRobotParam.ImageDTO imageDTO = jsonObject.toJavaObject(EnterpriseWeChatRobotParam.ImageDTO.class);
            robotParam.setMsgType(WeChatConstants.IMAGE_NAME);
            robotParam.setImage(imageDTO);
        }
        if (WeChatConstants.NEWS.equals(contentModel.getSendType())) {
            EnterpriseWeChatRobotParam.NewsDTO newsDTO = jsonObject.toJavaObject(EnterpriseWeChatRobotParam.NewsDTO.class);
            robotParam.setMsgType(WeChatConstants.NEWS_NAME);
            robotParam.setNews(newsDTO);
        }
        if (WeChatConstants.FILE.equals(contentModel.getSendType())) {
            EnterpriseWeChatRobotParam.FileDTO fileDTO = jsonObject.toJavaObject(EnterpriseWeChatRobotParam.FileDTO.class);
            robotParam.setMsgType(WeChatConstants.FILE_NAME);
            robotParam.setFile(fileDTO);
        }
        if (WeChatConstants.VOICE.equals(contentModel.getSendType())) {
            EnterpriseWeChatRobotParam.VoiceDTO voiceDTO = jsonObject.toJavaObject(EnterpriseWeChatRobotParam.VoiceDTO.class);
            robotParam.setMsgType(WeChatConstants.VOICE_NAME);
            robotParam.setVoice(voiceDTO);
        }
        return robotParam;
    }

}
