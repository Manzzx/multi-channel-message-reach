package com.metax.web.util.tencent;

import com.google.common.base.Throwables;
import com.metax.common.core.exception.ServiceException;
import com.metax.web.domain.centent.QueryTencentCloudSMSReceiptParam;
import com.metax.web.domain.centent.TencentSmsConfig;
import com.metax.web.vo.SmsRecord;
import com.metax.web.vo.SmsRecordPage;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.PullSmsSendStatus;
import com.tencentcloudapi.sms.v20210111.models.PullSmsSendStatusByPhoneNumberRequest;
import com.tencentcloudapi.sms.v20210111.models.PullSmsSendStatusByPhoneNumberResponse;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static com.metax.common.core.constant.MetaxDataConstants.CRON_TASK_FAIL;
import static com.metax.common.core.constant.MetaxDataConstants.CRON_TASK_SUCCESS;

/**
 * @Author: hanabi
 * @DateTime: 2023/11/1 11:52
 **/
@Slf4j
public class TencentCloudSMSReceiptPullByPhoneUtils {

    public static SmsRecordPage pull(TencentSmsConfig config, QueryTencentCloudSMSReceiptParam param) {
        List<SmsRecord> smsRecords = new ArrayList<>();
        SmsRecordPage smsRecordPage = new SmsRecordPage();
        try {
            Credential cred = new Credential(config.getSecretId(), config.getSecretKey());
            // 实例化一个http选项，可选的，没有特殊需求可以跳过
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint(config.getEndpoint());
            // 实例化一个client选项，可选的，没有特殊需求可以跳过
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            // 实例化要请求产品的client对象,clientProfile是可选的
            SmsClient client = new SmsClient(cred, config.getRegion(), clientProfile);
            // 实例化一个请求对象,每个接口都会对应一个request对象
            PullSmsSendStatusByPhoneNumberRequest req = new PullSmsSendStatusByPhoneNumberRequest();
            req.setBeginTime(param.getBeginTime());
            req.setOffset(param.getOffset());
            req.setLimit(param.getLimit());
            req.setPhoneNumber(param.getPhone());
            req.setSmsSdkAppId(config.getSmsSdkAppId());
            // 返回的resp是一个PullSmsSendStatusByPhoneNumberResponse的实例，与请求对象对应
            PullSmsSendStatusByPhoneNumberResponse res = client.PullSmsSendStatusByPhoneNumber(req);

            String requestId = res.getRequestId();
            String err = "(NOPASS可能是运营商网关拦截)";
            for (PullSmsSendStatus status : res.getPullSmsSendStatusSet()) {
                SmsRecord record = SmsRecord.builder()
                        .requestId(requestId)
                        .channelName("腾讯云")
                        .serialId(status.getSerialNo())
                        .receiveDate(LocalDateTime.ofInstant(Instant.ofEpochSecond(status.getUserReceiveTime()), ZoneId.systemDefault()))
                        .sendDate(null)
                        .status(("SUCCESS".equals(status.getReportStatus()) ? CRON_TASK_SUCCESS : CRON_TASK_FAIL))
                        .template("无返回结果")
                        .log(("SUCCESS".equals(status.getReportStatus()) ? status.getDescription() : status.getDescription().contains("NOPASS") ? status.getDescription() + err : status.getDescription()))
                        .content(status.getDescription())
                        .phone(status.getPhoneNumber())
                        .queryDate(LocalDateTime.now()).build();
                smsRecords.add(record);
            }

            smsRecordPage.setSmsRecords(smsRecords);
            smsRecordPage.setTotal(smsRecords.size());
            return smsRecordPage;
        } catch (Exception e) {
            log.error("腾讯云短信回执手机号方式查询异常:{}", Throwables.getStackTraceAsString(e));
            throw new ServiceException("腾讯云回执查询异常" + Throwables.getStackTraceAsString(e));
        }
    }
}
