package com.metax.web.service;

import com.metax.common.core.web.domain.AjaxResult;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author: hanabi
 * @DateTime: 2024/1/19 19:33
 **/
public interface MaterialService {

    public AjaxResult uploadEnterpriseWeChatRobotFileMaterial(MultipartFile multipartFile , Long sendAccount);

    public AjaxResult uploadEnterpriseWeChatRobotVoiceMaterial(MultipartFile multipartFile , Long sendAccount);
}
