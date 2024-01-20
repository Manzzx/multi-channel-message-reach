package com.metax.web.controller;

import com.metax.common.core.exception.ServiceException;
import com.metax.common.core.web.controller.BaseController;
import com.metax.common.core.web.domain.AjaxResult;
import com.metax.web.service.MaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

/**
 * 素材上传
 * @Author: hanabi
 * @DateTime: 2024/1/19 16:53
 **/
@RestController
@RequestMapping("/material")
public class MaterialController extends BaseController {

    @Autowired
    private MaterialService materialService;

    @PostMapping("/uploadFile")
    public AjaxResult uploadFile(MultipartFile multipartFile, String sendAccount) {
        if (Objects.isNull(sendAccount) || sendAccount.equals("null")) {
            throw new ServiceException("上传文件账号不能为空!");
        }
        return materialService.uploadEnterpriseWeChatRobotFileMaterial(multipartFile,Long.parseLong(sendAccount));
    }

    @PostMapping("/uploadVoice")
    public AjaxResult uploadVoice(MultipartFile multipartFile, String sendAccount) {
        if (Objects.isNull(sendAccount) || sendAccount.equals("null")) {
            throw new ServiceException("上传语音账号不能为空!");
        }
        return materialService.uploadEnterpriseWeChatRobotVoiceMaterial(multipartFile,Long.parseLong(sendAccount));
    }
}
