package com.metax.web.util.aliyun;

import cn.hutool.core.util.StrUtil;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.metax.common.core.exception.ServiceException;
import com.metax.web.domain.aliyun.AlibabaCloudSmsConfig;
import lombok.extern.slf4j.Slf4j;

import static com.metax.common.core.constant.MetaxDataConstants.OK;

/**
 * 阿里云短信发送工具类
 * @Author: hanabi
 */
@Slf4j
public class AlibabaCloudSMSSendUtils {

	/**
	 * 发送短信
	 * @param config 账号配置信息
	 * @param phoneNumbers 手机号
	 * @param param 参数（验证码）
	 */
	public static String sendMessage(AlibabaCloudSmsConfig config, String phoneNumbers, String param){
		DefaultProfile profile =DefaultProfile.getProfile(config.getRegionId(), config.getAccessKeyId(), config.getAccessSecret());
		IAcsClient client = new DefaultAcsClient(profile);

		SendSmsRequest request = new SendSmsRequest();
		request.setPhoneNumbers(phoneNumbers);
		request.setSignName(config.getSignName());
		request.setTemplateCode(config.getTemplateCode());
		if (!StrUtil.EMPTY.equals(param)){
			//有占位符数据
			request.setTemplateParam(param);
		}
		try {
			SendSmsResponse response = client.getAcsResponse(request);
			String code = response.getCode();
			if (OK.equals(code)){
				log.info("短信发送成功:"+ response);
				//返回回执ID
				return response.getBizId();
			}
			else {
				throw new ServiceException("响应码:"+code+" 报错信息:"+response.getMessage());
			}
		}catch (ClientException e) {
			throw new ServiceException(e.getMessage());
		}
	}
}
