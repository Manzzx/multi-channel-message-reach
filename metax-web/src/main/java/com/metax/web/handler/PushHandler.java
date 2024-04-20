package com.metax.web.handler;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson2.JSON;
import com.metax.common.core.exception.ServiceException;
import com.metax.web.domain.SendTaskInfo;
import com.metax.web.domain.push.getui.*;
import com.metax.web.dto.content.PushContentModel;
import com.metax.web.util.AccessTokenUtils;
import com.metax.web.util.AccountUtil;
import com.metax.web.util.DataUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.metax.common.core.constant.GeTuiConstants.*;
import static com.metax.common.core.constant.SendChanelUrlConstant.*;

/**
 * 个推push通知栏handler
 * https://docs.getui.com/getui/server/rest_v2/push/?id=doc-title-5
 *
 * @Author: hanabi
 * @DateTime: 2023/10/28 23:36
 **/
@Service
@Slf4j
public class PushHandler extends ChannelHandler {

    @Autowired
    private AccountUtil accountUtil;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private DataUtil dataUtil;

    @Override
    void doHandler(SendTaskInfo sendTaskInfo) {
        try {
            GeTuiConfig account = accountUtil.getAccount(sendTaskInfo.getMessageTemplate().getSendAccount(), GeTuiConfig.class);
            String token = getToken(sendTaskInfo, account);
            if (Objects.isNull(token)) {
                dataUtil.confirmSend(null, sendTaskInfo.getMessageId(), sendTaskInfo.getSendMessageKey(), sendTaskInfo.getSendTaskId(), new ServiceException("个推token获取出现异常!"));
                return;
            }
            String result = null;
            if (sendTaskInfo.getReceivers().size() == 1) {
                result = singlePush(sendTaskInfo, account, token);
                //如果碰到token过期重新获取token再次发起请求
                if (JSON.parseObject(result, SendPushResult.class).getCode().equals(10001)) {
                    result = singlePush(sendTaskInfo, account, getToken(sendTaskInfo, account));
                }
            } else {
                result = batchPush(sendTaskInfo, account, token);
                //如果碰到token过期重新获取token再次发起请求
                if (JSON.parseObject(result, SendPushResult.class).getCode().equals(10001)) {
                    result = batchPush(sendTaskInfo, account, getToken(sendTaskInfo, account));
                }
            }
            if (result == null || !JSON.parseObject(result, SendPushResult.class).getCode().equals(0)) {
                dataUtil.confirmSend(null, sendTaskInfo.getMessageId(), sendTaskInfo.getSendMessageKey(), sendTaskInfo.getSendTaskId(), new ServiceException(result));
                return;
            }

            dataUtil.confirmSend(result, sendTaskInfo.getMessageId(), sendTaskInfo.getSendMessageKey(), sendTaskInfo.getSendTaskId(), new Exception());
        } catch (Exception e) {
            dataUtil.confirmSend(null, sendTaskInfo.getMessageId(), sendTaskInfo.getSendMessageKey(), sendTaskInfo.getSendTaskId(), e);
            log.error("APP通知栏消息推送异常:" + e);
        }


    }

    /**
     * 个推cid批量推送 同步发送
     *
     * @param sendTaskInfo
     * @param account
     * @param token
     * @return
     */
    private String batchPush(SendTaskInfo sendTaskInfo, GeTuiConfig account, String token) {
        String url = GE_TUI_BASE_URL + account.getAppId() + GE_TUI_BATCH_PUSH_PATH;

        String taskId = buildTaskId(sendTaskInfo, account, token);
        if (StrUtil.isBlank(taskId)) {
            throw new ServiceException("批量推送获取taskId失败");
        }
        BatchSendPushParam batchSendPushParam = BatchSendPushParam.builder()
                .audience(BatchSendPushParam.AudienceVO.builder().cid(sendTaskInfo.getReceivers()).build())
                .taskId(taskId)
                .isAsync(false).build();

        String body = HttpRequest.post(url).header(Header.CONTENT_TYPE.getValue(), ContentType.JSON.getValue())
                .header("token", token)
                .body(JSON.toJSONString(batchSendPushParam))
                .timeout(2000)
                .execute().body();

        return body;
    }

    /**
     * 获取批量推任务id
     *
     * @param sendTaskInfo
     * @param account
     * @param token
     * @return
     */
    private String buildTaskId(SendTaskInfo sendTaskInfo, GeTuiConfig account, String token) {
        String url = GE_TUI_BASE_URL + account.getAppId() + GE_TUI_BATCH_PUSH_CREATE_TASK_PATH;
        SingleSendPushParam sendPushParam = buildParam(sendTaskInfo);
        String body = HttpRequest.post(url).header(Header.CONTENT_TYPE.getValue(), ContentType.JSON.getValue())
                .header("token", token)
                .body(JSON.toJSONString(sendPushParam))
                .timeout(2000)
                .execute().body();
        SendPushResult sendPushResult = JSON.parseObject(body, SendPushResult.class);
        if (sendPushResult.getCode().equals(0)) {
            return sendPushResult.getData().getString("taskid");
        }
        return null;
    }

    /**
     * cid单推
     *
     * @param sendTaskInfo
     * @param account
     * @return
     */
    private String singlePush(SendTaskInfo sendTaskInfo, GeTuiConfig account, String token) {
        String url = GE_TUI_BASE_URL + account.getAppId() + GE_TUI_SINGLE_PUSH_PATH;
        SingleSendPushParam singleSendPushParam = buildParam(sendTaskInfo);
        String body = HttpRequest.post(url).header(Header.CONTENT_TYPE.getValue(), ContentType.JSON.getValue())
                .header("token", token)
                .body(JSON.toJSONString(singleSendPushParam))
                .timeout(2000)
                .execute().body();

        return body;
    }

    /**
     * 获取token
     *
     * @param sendTaskInfo
     * @param account
     * @return
     */
    public String getToken(SendTaskInfo sendTaskInfo, GeTuiConfig account) {
        String token = stringRedisTemplate.opsForValue().get(GE_TUI_TOKEN_KEY + sendTaskInfo.getMessageTemplate().getSendAccount());
        if (StrUtil.isNotBlank(token)) {
            return token;
        }
        GeTuiTokenResultDTO.DataDTO dataDTO = AccessTokenUtils.getGeTuiToken(account);
        if (Objects.isNull(dataDTO)) {
            return null;
        }
        token = dataDTO.getToken();
        //token存进redis 并设置过期时间
        stringRedisTemplate.opsForValue().set(GE_TUI_TOKEN_KEY + sendTaskInfo.getMessageTemplate().getSendAccount(), token,
                EXPIRE_TIME, TimeUnit.MILLISECONDS);

        return token;
    }

    /**
     * 构建单推参数或者批量推创建消息参数
     *
     * @param sendTaskInfo
     * @return
     */
    private SingleSendPushParam buildParam(SendTaskInfo sendTaskInfo) {
        PushContentModel contentModel = JSON.parseObject(sendTaskInfo.getMessageTemplate().getMsgContent(), PushContentModel.class);

        String clickType = contentModel.getClickType();
        SingleSendPushParam.PushMessageVO.NotificationVO notificationVO =
                SingleSendPushParam.PushMessageVO.NotificationVO.builder().build();
        if (CLICK_TYPE_URL.equals(clickType)) {
            notificationVO.setUrl(contentModel.getUrl());
        }
        if (CLICK_TYPE_INTENT.equals(clickType)) {
            notificationVO.setIntent(contentModel.getIntent());
        }
        if (CLICK_TYPE_PAYLOAD.equals(clickType)) {
            notificationVO.setPayload(contentModel.getPayload());
        }
        if (CLICK_TYPE_PAYLOAD_CUSTOM.equals(clickType)) {
            notificationVO.setPayload(contentModel.getPayload());
        }
        notificationVO.setTitle(contentModel.getTitle());
        notificationVO.setBody(contentModel.getBody());
        notificationVO.setClickType(contentModel.getClickType());
        notificationVO.setChannelLevel(contentModel.getChannelLevel());

        SingleSendPushParam sendPushParam = SingleSendPushParam.builder()
                .requestId(String.valueOf(IdUtil.getSnowflake().nextId()))
                .audience(SingleSendPushParam.AudienceVO.builder().cid(sendTaskInfo.getReceivers()).build())
                .pushMessage(SingleSendPushParam.PushMessageVO.builder().notification(notificationVO).build())
                .build();

        return sendPushParam;
    }
}
