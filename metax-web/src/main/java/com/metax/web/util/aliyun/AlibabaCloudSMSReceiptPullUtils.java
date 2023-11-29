package com.metax.web.util.aliyun;

import cn.hutool.core.util.StrUtil;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.QuerySendDetailsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.QuerySendDetailsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.metax.web.domain.aliyun.AlibabaCloudSmsConfig;
import com.metax.web.domain.aliyun.QueryAlibabaCloudSMSReceiptParam;
import com.metax.web.vo.SmsRecord;
import com.metax.web.vo.SmsRecordPage;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.metax.common.core.constant.MetaxDataConstants.DAY_FORMAT_Y_M_D_H_M_S;
import static com.metax.common.core.constant.MetaxDataConstants.REDIS_DAY_KEY_FORMAT;

/**
 * 阿里云短信回执拉取
 *
 * @Author: hanabi
 * @DateTime: 2023/10/29 19:36
 **/

public class AlibabaCloudSMSReceiptPullUtils {

    //产品名称:云通信短信API产品,开发者无需替换
    static final String product = "Dysmsapi";

    //产品域名,开发者无需替换
    static final String domain = "dysmsapi.aliyuncs.com";

    /**
     * 查询短信详情
     *
     * @param config
     * @param param
     * @return
     * @throws ClientException
     */
    public static QuerySendDetailsResponse querySendDetails(AlibabaCloudSmsConfig config, QueryAlibabaCloudSMSReceiptParam param) throws ClientException {

        //可自助调整超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");

        //初始化acsClient,暂不支持region化
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", config.getAccessKeyId(), config.getAccessSecret());
        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
        IAcsClient acsClient = new DefaultAcsClient(profile);

        //组装请求对象
        QuerySendDetailsRequest request = new QuerySendDetailsRequest();
        //必填-号码
        request.setPhoneNumber(param.getPhone());
        //可选-流水号
        if (StrUtil.isNotBlank(param.getBizId())) {
            request.setBizId(param.getBizId());
        }
        //必填-发送日期 支持30天内记录查询，格式yyyyMMdd
        SimpleDateFormat ft = new SimpleDateFormat(REDIS_DAY_KEY_FORMAT);
        request.setSendDate(ft.format(new Date()));
        //必填-页大小
        request.setPageSize(param.getPageSize());
        //必填-当前页码
        request.setCurrentPage(param.getPageNum());

        //hint 此处可能会抛出异常，注意catch
        QuerySendDetailsResponse querySendDetailsResponse = acsClient.getAcsResponse(request);

        return querySendDetailsResponse;
    }

    public static SmsRecordPage pull(AlibabaCloudSmsConfig config, QueryAlibabaCloudSMSReceiptParam param) {
        List<SmsRecord> smsRecords = new ArrayList<>();
        SmsRecordPage smsRecordPage = new SmsRecordPage();
        Map<Long, String> statusMap = new HashMap<>();
        statusMap.put(1L, "发送失败");
        statusMap.put(2L, "发送中");
        statusMap.put(3L, "发送成功");
        try {
            QuerySendDetailsResponse querySendDetailsResponse = querySendDetails(config, param);
            String err ="";
            if (!querySendDetailsResponse.getCode().equals("OK")){
                err = "请求状态码:" + querySendDetailsResponse.getCode() + ",状态码的描述:" + querySendDetailsResponse.getMessage()
                        + ",运营商短信状态码:";
            }
            String requestId = querySendDetailsResponse.getRequestId();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DAY_FORMAT_Y_M_D_H_M_S);
            for (QuerySendDetailsResponse.SmsSendDetailDTO smsSendDetailDTO : querySendDetailsResponse.getSmsSendDetailDTOs()) {
                SmsRecord record = SmsRecord.builder()
                        .requestId(requestId)
                        .channelName("阿里云")
                        .serialId(StrUtil.isNotBlank(param.getBizId()) ? param.getBizId() : smsSendDetailDTO.getOutId())
                        .receiveDate(LocalDateTime.parse(smsSendDetailDTO.getReceiveDate(), formatter))
                        .sendDate(LocalDateTime.parse(smsSendDetailDTO.getSendDate(), formatter))
                        .status(statusMap.get(smsSendDetailDTO.getSendStatus()))
                        .template(smsSendDetailDTO.getTemplateCode())
                        .log(err+smsSendDetailDTO.getErrCode())
                        .content(smsSendDetailDTO.getContent())
                        .phone(smsSendDetailDTO.getPhoneNum())
                        .queryDate(LocalDateTime.now()).build();
                smsRecords.add(record);

            }
            smsRecordPage.setSmsRecords(smsRecords);
            smsRecordPage.setTotal(Long.parseLong(querySendDetailsResponse.getTotalCount()));
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return smsRecordPage;
    }
}
