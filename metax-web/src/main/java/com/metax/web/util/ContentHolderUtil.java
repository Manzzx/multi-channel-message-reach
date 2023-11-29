package com.metax.web.util;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
import com.metax.common.core.exception.ServiceException;
import com.metax.web.domain.MessageTemplate;
import com.metax.web.dto.content.WeChatServiceAccountContentModel;
import com.metax.web.service.WeChatServiceAccountService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.bean.template.WxMpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.PropertyPlaceholderHelper;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.metax.common.core.constant.MetaxDataConstants.*;
import static com.metax.common.core.constant.WeChatConstants.WECHAT_SERVICE_ACCOUNT_URL_NAME;

/**
 * 处理模板消息内容占位符工具
 *
 * @Author: hanabi
 */
@Slf4j
@Component
public class ContentHolderUtil {

    @Autowired
    private WeChatServiceAccountService weChatServiceAccountService;

    private static final PropertyPlaceholderHelper PROPERTY_PLACEHOLDER_HELPER =
            new PropertyPlaceholderHelper(PLACE_HOLDER_PREFIX, PLACE_HOLDER_SUFFIX);

    /**
     * 获取占位符变量名
     *
     * @param messageTemplate
     * @return
     */
    public List<String> getVariables(MessageTemplate messageTemplate) {

        String context = messageTemplate.getMsgContent();
        Set<String> skeys = new HashSet<>();
        Pattern pattern = Pattern.compile("\\$\\{([^}]+)\\}");
        Matcher matcher = pattern.matcher(context);

        while (matcher.find()) {
            String placeholder = matcher.group(0); // 获取完整的占位符，包括 "${" 和 "}"
            String key = matcher.group(1); // 获取占位符键，不包括 "${" 和 "}"
            //将占位符变量添加进set
            skeys.add(key);
        }
        if (WECHAT_SERVICE_ACCOUNT.equals(messageTemplate.getSendChannel())) {
            if (skeys.size() > 1) {
                throw new ServiceException("跳转链接不允许存在多个!");
            }
            //链接存在占位符 替换用户输入的占位符名称
            if (skeys.size() > 0) {
                skeys.clear();
                skeys.add(WECHAT_SERVICE_ACCOUNT_URL_NAME);
            }
            //微信服务号需要调用微信API查询占位符
            WeChatServiceAccountContentModel contentModel =
                    JSON.parseObject(messageTemplate.getMsgContent(), WeChatServiceAccountContentModel.class);
            WxMpTemplate wxMpTemplate = weChatServiceAccountService.queryWxTemplateDetailByTemplateId(messageTemplate.getSendAccount(), contentModel.getTemplateId());
            if (Objects.isNull(wxMpTemplate)) {
                throw new ServiceException("微信模板库没有该模板!");
            }
            Pattern wxPattern = Pattern.compile("\\{\\{([^\\.]+)\\..*?\\}\\}");
            Matcher wxMatcher = wxPattern.matcher(wxMpTemplate.getContent());
            while (wxMatcher.find()) {
                String key = wxMatcher.group(1);
                skeys.add(key);
            }

        }
        //转list
        return new ArrayList<>(skeys);
    }

    /**
     * 替换占位符
     */
    public String replacePlaceHolder(String content, String param) {

        Properties properties = null;
        try {
            properties = jsonToProperties(param);
        } catch (Exception e) {
            log.error("占位符数据解析失败:{}", e.getMessage());
            throw new ServiceException(e.getMessage());
        }
        assert properties != null;
        return PROPERTY_PLACEHOLDER_HELPER.replacePlaceholders(content, properties);
    }


    public static Properties jsonToProperties(String json) throws IOException {
        if (JSONUtil.isJson(json)) {
            JavaPropsMapper javaPropsMapper = new JavaPropsMapper();
            JSONObject jsonObject = JSONUtil.parseObj(json);
            return javaPropsMapper.writeValueAsProperties(jsonObject);
        }
        return null;
    }
}
