package com.metax.web.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.metax.common.core.context.SecurityContextHolder;
import com.metax.common.core.exception.ServiceException;
import com.metax.web.domain.ChannelAccount;
import com.metax.web.domain.aliyun.AlibabaCloudSmsConfig;
import com.metax.web.domain.aliyun.QueryAlibabaCloudSMSReceiptParam;
import com.metax.web.domain.centent.QueryTencentCloudSMSReceiptParam;
import com.metax.web.domain.centent.TencentSmsConfig;
import com.metax.web.dto.QuerySmsRecordDto;
import com.metax.web.service.IChannelAccountService;
import com.metax.web.service.SmsService;
import com.metax.web.util.aliyun.AlibabaCloudSMSReceiptPullUtilsV2;
import com.metax.web.util.tencent.TencentCloudSMSReceiptPullByPhoneUtils;
import com.metax.web.util.tencent.TencentCloudSMSReceiptPullUtils;
import com.metax.web.vo.SmsRecord;
import com.metax.web.vo.SmsRecordPage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.metax.common.core.constant.MetaxDataConstants.*;
import static com.metax.web.process.ReceiverCheckProcess.PHONE_REGEX_EXP;

/**
 * 短信回执服务
 *
 * @Author: hanabi
 * @DateTime: 2023/10/30 23:08
 **/
@Service
@Slf4j
public class SmsServiceImpl implements SmsService {

    @Autowired
    private IChannelAccountService channelAccountService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 拉取短信回执
     *
     * @param param
     * @return
     */
    @Override
    public SmsRecordPage pushRecord(QuerySmsRecordDto param) {
        if (StrUtil.isBlank(param.getAccount())) {
            throw new ServiceException("发送账号不能为空");
        }
        JSONObject jsonObject = null;
        String channelName = null;
        try {
            ChannelAccount channelAccount = channelAccountService.getById(param.getAccount());
            String accountConfig = channelAccount.getAccountConfig();
            jsonObject = JSONUtil.toBean(accountConfig, JSONObject.class);
            channelName = (String) jsonObject.get(SMS_SERVICE_KEY);
            if (Objects.isNull(channelName)){
                throw new ServiceException();
            }
        } catch (Exception e) {
            log.error("账号解析失败:{}",e.getMessage());
            throw new ServiceException("账号解析失败");
        }

        if (channelName.equals(ALIBABA_CLOUD_SERVICE_SMS_NAME)) {
            return pushAliSmsRecord(param, jsonObject);
        }
        if (channelName.equals(TENCENT_CLOUD_SERVICE_SMS_NAME)) {
            return pushTencentSmsRecord(param, jsonObject);
        }

        return null;
    }

    /**
     * 拉取腾讯云短信回执
     * 两种查找发送 1.按照手机号查找 2.按照SDKId查找 个人无法开通此功能
     *
     * @param params
     * @param jsonObject
     * @return
     */
    private SmsRecordPage pushTencentSmsRecord(QuerySmsRecordDto params, JSONObject jsonObject) {

        SmsRecordPage recordPage = null;
        try {
            TencentSmsConfig config = jsonObject.toBean(TencentSmsConfig.class);
            //如果手机号或者发送日期有一个不为空 进入按照手机号查找方式
            if (StrUtil.isNotBlank(params.getPhone()) || StrUtil.isNotBlank(params.getSendDate())) {
                if (StrUtil.hasBlank(params.getPageNum(), params.getPageSize(), params.getPhone(), params.getSendDate())) {
                    throw new ServiceException("腾讯云 手机号 发送日期不能为空");
                }
                String phone = "+86" + params.getPhone();
                // 解析日期字符串为LocalDate
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DAY_FORMAT_Y_M_D);
                LocalDate localDate = LocalDate.parse(params.getSendDate(), formatter);
                // 转换为Instant并获取时间戳
                Instant instant = localDate.atStartOfDay().toInstant(ZoneOffset.UTC);
                long timestamp = instant.getEpochSecond();
                int start = (Integer.parseInt(params.getPageNum()) - 1) * Integer.parseInt(params.getPageSize());
                QueryTencentCloudSMSReceiptParam param = QueryTencentCloudSMSReceiptParam.builder()
                        .phone(phone)
                        .beginTime(timestamp)
                        .offset((long) start)
                        .limit(Long.valueOf(params.getPageSize())).build();
                recordPage = TencentCloudSMSReceiptPullByPhoneUtils.pull(config, param);

            } else {
                //进入按照SDKId查找方式 个人无法开通
                recordPage = TencentCloudSMSReceiptPullUtils.pull(config);
                if (CollectionUtil.isNotEmpty(recordPage.getSmsRecords())) {
                    //腾讯云不支持分页 开始手动分页
                    List<SmsRecord> smsRecords = recordPage.getSmsRecords();
                    int start = (Integer.parseInt(params.getPageNum()) - 1) * Integer.parseInt(params.getPageSize());
                    //取数据结束下标和集合大小之间最小的值
                    int end = Math.min(start + Integer.parseInt(params.getPageSize()), smsRecords.size());
                    List<SmsRecord> records = smsRecords.subList(start, end);
                    recordPage.setSmsRecords(records);
                    recordPage.setTotal(smsRecords.size());
                }
            }
            //缓存查询过的短信回执记录
            if (CollectionUtil.isNotEmpty(recordPage.getSmsRecords())) {
                recordRecent(recordPage);
            }
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
        return recordPage;
    }

    /**
     * 拉取redis近期短信回执查询记录
     *
     * @param params
     * @return
     */
    @Override
    public SmsRecordPage pushRecentRecord(QuerySmsRecordDto params) {
        List<String> list = stringRedisTemplate.opsForList().range(SMS_RECORDS_KEY + SecurityContextHolder.getUserId(), 0, -1);
        if (CollectionUtil.isEmpty(list)) {
            return SmsRecordPage.builder().smsRecords(new ArrayList<>()).total(0L).build();
        }
        List<SmsRecord> smsRecords = list.stream().map(s -> JSON.parseObject(s, SmsRecord.class)).collect(Collectors.toList());
        int start = (Integer.parseInt(params.getPageNum()) - 1) * Integer.parseInt(params.getPageSize());
        //取数据结束下标和集合大小之间最小的值
        int end = Math.min(start + Integer.parseInt(params.getPageSize()), smsRecords.size());
        List<SmsRecord> records = smsRecords.subList(start, end);
        return SmsRecordPage.builder().smsRecords(records).total(smsRecords.size()).build();

    }

    /**
     * 清空近期回执记录
     *
     * @return
     */
    @Override
    public boolean clear() {
        Boolean delete = stringRedisTemplate.delete(SMS_RECORDS_KEY + SecurityContextHolder.getUserId());
        if (Objects.isNull(delete)) {
            return false;
        }
        return delete.booleanValue();
    }

    /**
     * 拉取阿里云短信回执
     *
     * @param param
     * @param jsonObject
     * @return
     */
    public SmsRecordPage pushAliSmsRecord(QuerySmsRecordDto param, JSONObject jsonObject) {
        if (StrUtil.hasBlank(param.getPageNum(), param.getPageSize(), param.getPhone(), param.getSendDate())) {
            throw new ServiceException("阿里云 手机号 发送日期不能为空");
        }
        SmsRecordPage recordPage;
        try {
            AlibabaCloudSmsConfig config = jsonObject.toBean(AlibabaCloudSmsConfig.class);
            LocalDate date = LocalDate.parse(param.getSendDate());
            String format = date.format(DateTimeFormatter.ofPattern(REDIS_DAY_KEY_FORMAT));
            QueryAlibabaCloudSMSReceiptParam receiptParam = QueryAlibabaCloudSMSReceiptParam.builder()
                    .phone(param.getPhone())
                    .bizId(param.getSerialId())
                    .pageNum(Long.parseLong(param.getPageNum()))
                    .pageSize(Long.parseLong(param.getPageSize()))
                    .sendDate(format).build();

            recordPage = AlibabaCloudSMSReceiptPullUtilsV2.pull(config, receiptParam);
            //缓存查询过的短信回执记录
            if (CollectionUtil.isNotEmpty(recordPage.getSmsRecords())) {
                recordRecent(recordPage);
            }

        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }

        return recordPage;
    }

    private void recordRecent(SmsRecordPage recordPage) {
        List<String> list = recordPage.getSmsRecords().stream().map(JSON::toJSONString).collect(Collectors.toList());
        stringRedisTemplate.opsForList()
                .leftPushAll(SMS_RECORDS_KEY + SecurityContextHolder.getUserId(), list);
        //设置过期时间为一个月 单位秒
        stringRedisTemplate.expire(SMS_RECORDS_KEY + SecurityContextHolder.getUserId(), SMS_RECORD_EXPIRE_TIME, TimeUnit.SECONDS);
    }
}
