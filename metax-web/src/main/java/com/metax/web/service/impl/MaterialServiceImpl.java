package com.metax.web.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.metax.common.core.web.domain.AjaxResult;
import com.metax.web.domain.weChat.EnterpriseWeChatRobotConfig;
import com.metax.web.domain.weChat.EnterpriseWeChatRootResult;
import com.metax.web.dto.content.EnterpriseWeChatRobotContentModel;
import com.metax.web.service.MaterialService;
import com.metax.web.util.AccountUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

import static com.metax.common.core.constant.SendChanelUrlConstant.ENTERPRISE_WE_CHAT_ROBOT_URL;

/**
 * 素材上传
 *
 * @Author: hanabi
 * @DateTime: 2024/1/19 19:34
 **/
@Service
@Slf4j
public class MaterialServiceImpl implements MaterialService {

    @Autowired
    private AccountUtil accountUtil;

    /**
     * 上传文件
     * https://developer.work.weixin.qq.com/document/path/91770#%E6%96%87%E4%BB%B6%E4%B8%8A%E4%BC%A0%E6%8E%A5%E5%8F%A3
     *
     * @param multipartFile
     * @param sendAccount
     * @return
     */
    @Override
    public AjaxResult uploadEnterpriseWeChatRobotFileMaterial(MultipartFile multipartFile, Long sendAccount) {
        EnterpriseWeChatRootResult result = getEnterpriseWeChatRootResult(multipartFile, sendAccount,"file");
        if (result.getErrCode() == 0) {
            return AjaxResult.success(result.getMediaId());
        }
        return AjaxResult.error(result.getErrMsg());
    }

    /**
     * 上传语音
     * @param multipartFile
     * @param sendAccount
     * @return
     */
    @Override
    public AjaxResult uploadEnterpriseWeChatRobotVoiceMaterial(MultipartFile multipartFile, Long sendAccount) {
        EnterpriseWeChatRootResult result = getEnterpriseWeChatRootResult(multipartFile, sendAccount,"voice");
        if (result.getErrCode() == 0) {
            return AjaxResult.success(result.getMediaId());
        }
        return AjaxResult.error(result.getErrMsg());
    }

    private EnterpriseWeChatRootResult getEnterpriseWeChatRootResult(MultipartFile multipartFile, Long sendAccount,String type) {
        EnterpriseWeChatRobotConfig config = accountUtil.getAccount(sendAccount, EnterpriseWeChatRobotConfig.class);
        String key = config.getWebhook().substring(config.getWebhook().indexOf("=") + 1);
        String url = ENTERPRISE_WE_CHAT_ROBOT_URL.replace("<KEY>", key).replace("<TYPE>", type);
        File file = null;
        try {
            file = new File(multipartFile.getOriginalFilename());
            FileUtils.writeByteArrayToFile(file, multipartFile.getBytes());
        } catch (IOException e) {
            log.error("文件上传失败:{}", e.getMessage());
        }
        String response = HttpRequest.post(url)
                .form(IdUtil.fastSimpleUUID(), file)
                .execute().body();
        return JSON.parseObject(response, EnterpriseWeChatRootResult.class);
    }
}
