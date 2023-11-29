package com.metax.web.service;

import me.chanjar.weixin.mp.bean.template.WxMpTemplate;

import java.util.List;

/**
 * @Author: hanabi
 * @DateTime: 2023/10/26 9:57
 **/
public interface WeChatServiceAccountService {

    public List<WxMpTemplate> queryWxTemplateList(Long id);

    WxMpTemplate queryWxTemplateDetailByTemplateId(Long id, String templateId);
}
