package com.metax.web.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.metax.common.core.constant.MetaxDataConstants;
import com.metax.common.core.context.SecurityContextHolder;
import com.metax.system.api.domain.SysUser;
import com.metax.web.domain.MessageTemplate;
import com.metax.web.service.EChartsDataService;
import com.metax.web.service.IMessageTemplateService;
import com.metax.web.service.ISysUserService;
import com.metax.web.util.RedisKeyUtil;
import com.metax.web.vo.echarts.IndexEChartsData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.metax.common.core.constant.MetaxDataConstants.*;

/**
 * 首页eCharts图service
 * @Author: hanabi
 * @DateTime: 2023/11/2 21:20
 **/
@Service
@Slf4j
public class EChartsDataServiceImpl implements EChartsDataService {

    @Autowired
    private IMessageTemplateService messageTemplateService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private DataServiceImpl dataService;

    @Autowired
    private ISysUserService userService;

    public static final ThreadLocal<List<MessageTemplate>> TEMPLATE_LIST = ThreadLocal.withInitial(ArrayList::new);

    @Override
    public IndexEChartsData getIndexEChartsData(String day, Long userId) {
        //先计算出最新数据存进redis
        dataService.getCurrentDayData(0, 0, day, userId);

        if (userId == null || userId == 0){
            userId = SecurityContextHolder.getUserId();
        }
        IndexEChartsData.ShowPanelData showPanelData = computeShowPanelData(userId);
        IndexEChartsData.ShowPieChartAuditData showPieChartAuditData = computeShowPieChartAuditData();
        IndexEChartsData.ShowPieChartData showPieChartData = computeShowPieChartData();
        IndexEChartsData.ShowBarChartData showBarChartData = computeShowBarChartData(day,userId);
        IndexEChartsData.ShowLineChartData showLineChartData = computeShowLineChartData(day,userId);
        dataService.clearThreadData();
        TEMPLATE_LIST.remove();

        return IndexEChartsData.builder()
                .showPanelData(showPanelData)
                .showPieChartData(showPieChartData)
                .showPieChartAuditData(showPieChartAuditData)
                .showBarChartData(showBarChartData)
                .showLineChartData(showLineChartData).build();

    }

    /**
     * 根据用户id获取首页数据
     *
     * @param day
     * @param userId
     * @return
     */
    @Override
    public IndexEChartsData getUserIndexEChartData(String day, Long userId) {
        if (userId == null || userId == 0) {
            LambdaQueryWrapper<SysUser> wrapper = Wrappers.lambdaQuery();
            wrapper.last("LIMIT 1");
            SysUser user = userService.getOne(wrapper);
            if (user == null) {
                return null;
            }
            userId = user.getUserId();
        }
        return getIndexEChartsData(day, userId);

    }


    /**
     * 计算线形图消息前七天的发送情况
     *
     * @param day
     * @return
     */
    public IndexEChartsData.ShowLineChartData computeShowLineChartData(String day,Long userId) {
        List<Integer> successList = new ArrayList<>();
        List<Integer> sendingList = new ArrayList<>();
        List<Integer> failList = new ArrayList<>();
        // 获取前七天日期
        List<String> previousSevenDays = getPreviousSevenDays(day);
        for (String d : previousSevenDays) {
            //获取每一天的发送状况
            String successKey = RedisKeyUtil.getSuccessRedisKey(userId, d);
            String failKey = RedisKeyUtil.getFailRedisKey(userId, d);
            String sendingKey = RedisKeyUtil.getSendingRedisKey(userId, d);
            //成功
            String s1 = stringRedisTemplate.opsForValue().get(successKey);
            if (s1 == null || s1.equals("0")) {
                successList.add(0);
            } else {
                successList.add(Integer.parseInt(s1));
            }
            //失败
            String s2 = stringRedisTemplate.opsForValue().get(failKey);
            if (s2 == null || s2.equals("0")) {
                failList.add(0);
            } else {
                failList.add(Integer.parseInt(s2));
            }
            //发送中
            String s3 = stringRedisTemplate.opsForValue().get(sendingKey);
            if (s3 == null || s3.equals("0")) {
                sendingList.add(0);
            } else {
                sendingList.add(Integer.parseInt(s3));
            }
        }
        IndexEChartsData.ShowLineChartData.Series.Data data = IndexEChartsData.ShowLineChartData.Series.Data.builder()
                .successList(successList)
                .failList(failList)
                .sendingList(sendingList).build();

        return IndexEChartsData.ShowLineChartData.builder()
                .xAxis(IndexEChartsData.ShowLineChartData.XAxis.builder().data(previousSevenDays).build())
                .series(IndexEChartsData.ShowLineChartData.Series.builder().data(data).build()).build();
    }

    /**
     * 计算消息渠道柱状图前七天的发送情况
     *
     * @param day
     * @return
     */
    public IndexEChartsData.ShowBarChartData computeShowBarChartData(String day,Long userId) {
        // 获取前七天日期
        List<String> previousSevenDays = getPreviousSevenDays(day);

        IndexEChartsData.ShowBarChartData.XAxis xAxis = IndexEChartsData.ShowBarChartData.XAxis.builder().data(previousSevenDays).build();
        //存放七天每个渠道的发送情况
        List<Map<Integer, Integer>> channelMapList = new ArrayList<>();
        // 获取前七天情况
        for (int i = 0; i < previousSevenDays.size(); i++) {
            String key = RedisKeyUtil.getSendChannelCountRedisKey(userId, previousSevenDays.get(i));
            Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(key);
            if (CollectionUtil.isEmpty(entries)) {
                //当天没有发过消息，渠道信息为空
                channelMapList.add(null);
            } else {
                //Map<Object, Object>转Map<Integer, Integer>
                Map<Integer, Integer> newMap = new HashMap<>();
                for (Map.Entry<Object, Object> entry : entries.entrySet()) {
                    Integer newKey = Integer.parseInt(String.valueOf(entry.getKey()));
                    Integer newValue = Integer.parseInt(String.valueOf(entry.getValue()));
                    newMap.put(newKey, newValue);
                }
                channelMapList.add(newMap);
            }
        }
        List<IndexEChartsData.ShowBarChartData.Series> seriesList = new ArrayList<>();
        for (Map.Entry<Integer, String> entry : dataService.getDataUtil().channelCNMapping().entrySet()) {
            //每一个渠道前七天发送数量
            List<Integer> channelData = new ArrayList<>();
            //遍历每一天的渠道发送情况
            for (int i = 0; i < channelMapList.size(); i++) {
                if (channelMapList.get(i) == null) {
                    //该渠道这天没有发送过消息
                    channelData.add(0);
                } else {
                    channelData.add(channelMapList.get(i).get(entry.getKey()));
                }
            }

            //每个渠道的Y轴信息
            seriesList.add(IndexEChartsData.ShowBarChartData.Series.builder().name(entry.getValue()).data(channelData).build());
        }

        return IndexEChartsData.ShowBarChartData.builder().xAxis(xAxis).series(seriesList).build();
    }

    /**
     * 计算当天消息发送情况
     *
     * @return
     */
    public IndexEChartsData.ShowPieChartData computeShowPieChartData() {

        return IndexEChartsData.ShowPieChartData.builder()
                .success(DataServiceImpl.SUCCESS_TEM.get())
                .sending(DataServiceImpl.SENDING_TEM.get())
                .fail(DataServiceImpl.FAIL_TEM.get()).build();
    }

    /**
     * 计算审核模板扇形图情况
     *
     * @return
     */
    public IndexEChartsData.ShowPieChartAuditData computeShowPieChartAuditData() {
        int passAudit = 0;
        int failAudit = 0;
        int waitAudit = 0;
        if (CollectionUtil.isNotEmpty(TEMPLATE_LIST.get())) {
            for (MessageTemplate messageTemplate : TEMPLATE_LIST.get()) {
                if (AUDIT_WAITING.equals(messageTemplate.getAuditStatus())) {
                    waitAudit += 1;
                }
                if (AUDIT_PASS.equals(messageTemplate.getAuditStatus())) {
                    passAudit += 1;
                }
                if (AUDIT_REJECTED.equals(messageTemplate.getAuditStatus())) {
                    failAudit += 1;
                }
            }
        }

        return IndexEChartsData.ShowPieChartAuditData.builder().passAudit(passAudit)
                .failAudit(failAudit)
                .waitAudit(waitAudit).build();
    }

    /**
     * 计算第一层版块数据 总下发人数 今日下发人数 模板数 已启动目标数
     *
     * @return
     */
    public IndexEChartsData.ShowPanelData computeShowPanelData(Long userId) {
        String s = stringRedisTemplate.opsForValue().get(MetaxDataConstants.USER_SEND_NUMBER + userId);
        int total = s != null ? Integer.parseInt(s) : 0;
        int sendTotalOfDay = DataServiceImpl.SEND_TOTAL_OF_DAY_TEM.get();
        SysUser user = dataService.getDataUtil().getSysUserService().lambdaQuery().eq(SysUser::getUserId,userId).one();
        List<MessageTemplate> templates = messageTemplateService.lambdaQuery().eq(MessageTemplate::getCreator, user.getUserName()).list();
        TEMPLATE_LIST.set(templates);
        int tempNum = templates.size();
        List<MessageTemplate> startTemp = templates.stream().filter(temp -> MSG_START.equals(temp.getMsgStatus())).collect(Collectors.toList());
        int startTempNum = startTemp.size();

        return IndexEChartsData.ShowPanelData.builder()
                .totalNum(total)
                .dayNum(sendTotalOfDay)
                .temNum(tempNum)
                .startNum(startTempNum).build();
    }


    /**
     * 获取前七天日期集合
     *
     * @param day
     * @return
     */
    public List<String> getPreviousSevenDays(String day) {
        LocalDate date;
        if (StrUtil.isNotBlank(day) && !day.equals("null")) {
            date = LocalDate.parse(day);
        } else {
            // 获取当天日期
            date = LocalDate.now();
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(REDIS_DAY_KEY_FORMAT);
        String dayFormatted = date.format(formatter);

        List<String> previousSevenDays = new ArrayList<>();
        // 获取前六天日期
        for (int i = 6; i > 0; i--) {
            LocalDate previousDay = date.minusDays(i); // i 表示前i天
            String previousDayFormatted = previousDay.format(formatter);
            previousSevenDays.add(previousDayFormatted);
        }
        previousSevenDays.add(dayFormatted);
        return previousSevenDays;
    }
}
