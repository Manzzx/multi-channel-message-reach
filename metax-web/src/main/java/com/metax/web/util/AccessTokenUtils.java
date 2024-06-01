package com.metax.web.util;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.http.HttpException;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson2.JSON;
import com.google.common.base.Throwables;
import com.metax.common.core.exception.ServiceException;
import com.metax.web.domain.push.getui.GeTuiConfig;
import com.metax.web.domain.push.getui.GeTuiTokenResultDTO;
import com.metax.web.domain.push.getui.QueryTokenParamDTO;
import lombok.extern.slf4j.Slf4j;

import static com.metax.common.core.constant.SendChanelUrlConstant.GE_TUI_AUTH;
import static com.metax.common.core.constant.SendChanelUrlConstant.GE_TUI_BASE_URL;

/**
 * token工具类
 *
 * @Author: hanabi
 * @DateTime: 2023/10/28 21:49
 **/
@Slf4j
public class AccessTokenUtils {

    /**
     * 获取个推用户token
     * https://docs.getui.com/getui/server/rest_v2/token/
     *
     * @param account
     * @return
     */
    public static GeTuiTokenResultDTO.DataDTO getGeTuiToken(GeTuiConfig account) {
        //个推token请求地址
        try {
            String url = GE_TUI_BASE_URL + account.getAppId() + GE_TUI_AUTH;
            long timestamp = System.currentTimeMillis();
            String sign = SecureUtil.sha256(account.getAppKey() + timestamp + account.getMasterSecret());

            QueryTokenParamDTO paramDTO = QueryTokenParamDTO.builder()
                    .sign(sign).appKey(account.getAppKey()).timestamp(String.valueOf(timestamp)).build();
            //发送请求
            String body = HttpRequest.post(url).header(Header.CONTENT_TYPE.getValue(), ContentType.JSON.getValue())
                    .body(JSON.toJSONString(paramDTO))
                    .timeout(20000)
                    .execute().body();
            GeTuiTokenResultDTO result = JSON.parseObject(body, GeTuiTokenResultDTO.class);
            if (result.getCode().equals(0)) {
                return result.getData();
            }
        } catch (Exception e) {
            log.error("个推token 获取出现异常:{}", Throwables.getStackTraceAsString(e));
            return null;
        }
        return null;
    }
}
