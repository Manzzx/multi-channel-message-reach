package com.metax.web.controller;

import com.metax.common.core.web.controller.BaseController;
import com.metax.common.core.web.domain.AjaxResult;
import com.metax.web.service.WeChatServiceAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: hanabi
 * @DateTime: 2023/10/26 8:45
 **/
@RestController
@RequestMapping("/weChatServiceAccount")
public class WeChatServiceAccountController extends BaseController {

    @Autowired
    private WeChatServiceAccountService weChatServiceAccountService;

    /**
     * 根据微信服务号账号id查询服务号模板列表
     *
     * @param id
     * @return
     */
    @GetMapping("/template/list/{id}")
    public AjaxResult templateList(@PathVariable Long id) {
        return success(weChatServiceAccountService.queryWxTemplateList(id));
    }

    /**
     * 根据微信服务号账号id查询服务号模板 找出某一个模板信息
     *
     * @param id
     * @param templateId
     * @return
     */
    @GetMapping("/template/detail")
    public AjaxResult templateDetail(Long id, String templateId) {
        return success(weChatServiceAccountService.queryWxTemplateDetailByTemplateId(id, templateId));
    }
}
