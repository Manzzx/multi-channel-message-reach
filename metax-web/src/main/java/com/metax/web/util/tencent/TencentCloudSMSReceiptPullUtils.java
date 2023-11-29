package com.metax.web.util.tencent;

import cn.hutool.core.util.StrUtil;
import com.metax.common.core.exception.ServiceException;
import com.metax.web.domain.centent.TencentSmsConfig;
import com.metax.web.dto.QuerySmsRecordDto;
import com.metax.web.vo.SendTaskInfoVo;
import com.metax.web.vo.SmsRecord;
import com.metax.web.vo.SmsRecordPage;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.PullSmsSendStatus;
import com.tencentcloudapi.sms.v20210111.models.PullSmsSendStatusRequest;
import com.tencentcloudapi.sms.v20210111.models.PullSmsSendStatusResponse;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static com.metax.common.core.constant.MetaxDataConstants.CRON_TASK_FAIL;
import static com.metax.common.core.constant.MetaxDataConstants.CRON_TASK_SUCCESS;

/**
 * 腾讯云短信回执拉取
 *
 * @Author: hanabi
 * @DateTime: 2023/10/31 23:32
 **/
@Slf4j
public class TencentCloudSMSReceiptPullUtils {

    public static SmsRecordPage pull(TencentSmsConfig config) {
        List<SmsRecord> smsRecords = new ArrayList<>();
        SmsRecordPage smsRecordPage = new SmsRecordPage();
        try {

            Credential cred = new Credential(config.getSecretId(), config.getSecretKey());


            HttpProfile httpProfile = new HttpProfile();

            httpProfile.setReqMethod("POST");
            /* SDK有默认的超时时间，非必要请不要进行调整
             * 如有需要请在代码中查阅以获取最新的默认值 */
            httpProfile.setConnTimeout(60);
            /* 指定接入地域域名，默认就近地域接入域名为 sms.tencentcloudapi.com ，也支持指定地域域名访问，例如广州地域的域名为 sms.ap-guangzhou.tencentcloudapi.com */
            httpProfile.setEndpoint(config.getEndpoint());


            /* 非必要步骤:
             * 实例化一个客户端配置对象，可以指定超时时间等配置 */
            ClientProfile clientProfile = new ClientProfile();
            /* SDK默认用TC3-HMAC-SHA256进行签名
             * 非必要请不要修改这个字段 */
            clientProfile.setSignMethod("HmacSHA256");
            clientProfile.setHttpProfile(httpProfile);

            SmsClient client = new SmsClient(cred, config.getRegion(), clientProfile);



            /* 实例化一个请求对象，根据调用的接口和实际情况，可以进一步设置请求参数
             * 您可以直接查询SDK源码确定接口有哪些属性可以设置
             * 属性可能是基本类型，也可能引用了另一个数据结构
             * 推荐使用IDE进行开发，可以方便的跳转查阅各个接口和数据结构的文档说明 */
            PullSmsSendStatusRequest req = new PullSmsSendStatusRequest();


            /* 短信应用ID: 短信SdkAppId在 [短信控制台] 添加应用后生成的实际SdkAppId，示例如1400006666 */
            req.setSmsSdkAppId(config.getSmsSdkAppId());

            // 设置拉取最大条数，最多100条
            Long limit = 100L;
            req.setLimit(limit);


            /* 通过 client 对象调用 PullSmsSendStatus 方法发起请求。注意请求方法名与请求对象是对应的
             * 返回的 res 是一个 PullSmsSendStatusResponse 类的实例，与请求对象对应 */
            PullSmsSendStatusResponse res = client.PullSmsSendStatus(req);
            String requestId = res.getRequestId();
            for (PullSmsSendStatus status : res.getPullSmsSendStatusSet()) {
                SmsRecord record = SmsRecord.builder()
                        .requestId(requestId)
                        .channelName("腾讯云")
                        .serialId(status.getSerialNo())
                        .receiveDate(LocalDateTime.ofInstant(Instant.ofEpochSecond(status.getUserReceiveTime()), ZoneId.systemDefault()))
                        .sendDate(LocalDateTime.now())
                        .status(("SUCCESS".equals(status.getReportStatus()) ? CRON_TASK_SUCCESS : CRON_TASK_FAIL))
                        .template("无返回结果")
                        .log(status.getDescription())
                        .content(status.getDescription())
                        .phone(status.getPhoneNumber())
                        .queryDate(LocalDateTime.now()).build();
                smsRecords.add(record);
            }

            smsRecordPage.setSmsRecords(smsRecords);
            smsRecordPage.setTotal(smsRecords.size());

            return smsRecordPage;

        } catch (Exception e) {
            log.error("腾讯云短信回执查询异常:{}",e.getMessage());
            throw new ServiceException(e.getMessage());
        }

    }
}
