package com.metax.web.util.aliyun;

import cn.hutool.core.util.StrUtil;
import com.aliyun.dysmsapi20170525.models.QuerySendDetailsRequest;
import com.aliyun.dysmsapi20170525.models.QuerySendDetailsResponse;
import com.aliyun.dysmsapi20170525.models.QuerySendDetailsResponseBody;
import com.google.common.base.Throwables;
import com.metax.common.core.exception.ServiceException;
import com.metax.web.domain.aliyun.AlibabaCloudSmsConfig;
import com.metax.web.domain.aliyun.QueryAlibabaCloudSMSReceiptParam;
import com.metax.web.vo.SmsRecord;
import com.metax.web.vo.SmsRecordPage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.metax.common.core.constant.MetaxDataConstants.DAY_FORMAT_Y_M_D_H_M_S;


/**
 * 查询短信详情
 * https://help.aliyun.com/zh/sms/developer-reference/api-dysmsapi-2017-05-25-querysenddetails
 *
 * @Author: hanabi
 * @DateTime: 2023/10/29 20:55
 **/
public class AlibabaCloudSMSReceiptPullUtilsV2 {

    public static com.aliyun.dysmsapi20170525.Client createClient(String accessKeyId, String accessKeySecret) throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config();
        config.accessKeyId = accessKeyId;
        config.accessKeySecret = accessKeySecret;
        config.endpoint = "dysmsapi.aliyuncs.com";
        return new com.aliyun.dysmsapi20170525.Client(config);
    }

    public static SmsRecordPage pull(AlibabaCloudSmsConfig config, QueryAlibabaCloudSMSReceiptParam param) {
        List<SmsRecord> smsRecords = new ArrayList<>();
        SmsRecordPage smsRecordPage = new SmsRecordPage();
        try {
            com.aliyun.dysmsapi20170525.Client client = createClient(config.getAccessKeyId(), config.getAccessSecret());

            QuerySendDetailsRequest queryReq = new QuerySendDetailsRequest()
                    .setPhoneNumber(com.aliyun.teautil.Common.assertAsString(param.getPhone()))
                    .setBizId(StrUtil.isNotBlank(param.getBizId()) ? param.getBizId() : null)
                    .setSendDate(param.getSendDate())
                    .setPageSize(param.getPageSize())
                    .setCurrentPage(param.getPageNum());

            QuerySendDetailsResponse queryResp = client.querySendDetails(queryReq);

            java.util.List<QuerySendDetailsResponseBody.QuerySendDetailsResponseBodySmsSendDetailDTOsSmsSendDetailDTO> dtos = queryResp.body.smsSendDetailDTOs.smsSendDetailDTO;
            String err ="";
            if (!queryResp.body.getCode().equals("OK")){
                err = "请求状态码:" + queryResp.body.getCode() + ",状态码的描述:" + queryResp.body.getMessage()
                        + ",运营商短信状态码:";
            }
            String requestId = queryResp.body.requestId;
            // 处理结果
            Map<Long, String> statusMap = new HashMap<>();
            statusMap.put(1L, "发送失败");
            statusMap.put(2L, "发送中");
            statusMap.put(3L, "发送成功");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DAY_FORMAT_Y_M_D_H_M_S);
            for (QuerySendDetailsResponseBody.QuerySendDetailsResponseBodySmsSendDetailDTOsSmsSendDetailDTO dto : dtos) {
                SmsRecord record = SmsRecord.builder()
                        .requestId(requestId)
                        .channelName("阿里云")
                        .serialId(StrUtil.isNotBlank(param.getBizId()) ? param.getBizId() : dto.outId)
                        .receiveDate(LocalDateTime.parse(dto.receiveDate, formatter))
                        .sendDate(LocalDateTime.parse(dto.sendDate, formatter))
                        .status(statusMap.get(dto.sendStatus))
                        .template(dto.templateCode)
                        .log(err+dto.errCode)
                        .content(dto.content)
                        .phone(dto.phoneNum)
                        .queryDate(LocalDateTime.now()).build();
                smsRecords.add(record);
            }
            smsRecordPage.setSmsRecords(smsRecords);
            smsRecordPage.setTotal(Long.parseLong(queryResp.body.totalCount));
        } catch (Exception e) {
            throw new ServiceException(Throwables.getStackTraceAsString(e));
        }
        return smsRecordPage;
    }
}
