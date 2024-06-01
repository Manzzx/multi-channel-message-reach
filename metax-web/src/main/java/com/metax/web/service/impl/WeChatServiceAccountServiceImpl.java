package com.metax.web.service.impl;

import com.google.common.base.Throwables;
import com.metax.common.core.exception.ServiceException;
import com.metax.web.service.WeChatServiceAccountService;
import com.metax.web.util.AccountUtil;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.template.WxMpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @Author: hanabi
 * @DateTime: 2023/10/26 10:00
 **/
@Service
@Slf4j
public class WeChatServiceAccountServiceImpl implements WeChatServiceAccountService {

    @Autowired
    private AccountUtil accountUtil;

    @Override
    public List<WxMpTemplate> queryWxTemplateList(Long id) {
        WxMpService wxMpService = accountUtil.getAccount(id, WxMpService.class);
        List<WxMpTemplate> allPrivateTemplate;
        try {
            allPrivateTemplate = wxMpService.getTemplateMsgService().getAllPrivateTemplate();
        } catch (Exception e) {
            log.error("微信服务号模板信息获取失败 :{}", Throwables.getStackTraceAsString(e));
            throw new ServiceException(Throwables.getStackTraceAsString(e));
        }
        return allPrivateTemplate;
    }

    @Override
    public WxMpTemplate queryWxTemplateDetailByTemplateId(Long id, String templateId) {
        WxMpService wxMpService = accountUtil.getAccount(id, WxMpService.class);
        List<WxMpTemplate> allPrivateTemplate;
        try {
            allPrivateTemplate = wxMpService.getTemplateMsgService().getAllPrivateTemplate();
            Optional<WxMpTemplate> wxMpTemplateOptional = allPrivateTemplate.stream()
                    .filter(template -> templateId.equals(template.getTemplateId())).findFirst();
            return wxMpTemplateOptional.orElse(null);
        } catch (Exception e) {
            log.error("微信服务号模板信息获取失败 :{}", Throwables.getStackTraceAsString(e));
            throw new ServiceException(Throwables.getStackTraceAsString(e));
        }
    }
}
