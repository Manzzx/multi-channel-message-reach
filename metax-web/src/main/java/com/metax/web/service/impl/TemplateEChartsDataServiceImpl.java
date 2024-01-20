package com.metax.web.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.metax.common.core.context.SecurityContextHolder;
import com.metax.web.domain.MessageTemplate;
import com.metax.web.domain.content.SendContent;
import com.metax.web.service.IMessageTemplateService;
import com.metax.web.service.TemplateEChartsDataService;
import com.metax.web.util.DataUtil;
import com.metax.web.util.RedisKeyUtil;
import com.metax.web.vo.echarts.TemplateEChartsData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static com.metax.common.core.constant.MetaxDataConstants.*;
import static com.metax.common.core.constant.MetaxDataConstants.MSG_SENDING;

/**
 * 模板分析
 *
 * @Author: hanabi
 * @DateTime: 2023/11/8 11:21
 **/
@Service
@Slf4j
public class TemplateEChartsDataServiceImpl implements TemplateEChartsDataService {

    @Autowired
    private IMessageTemplateService messageTemplateService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private DataUtil dataUtil;
    //展示在页面的最近发送用户的最大值
    private static final int userSize = 9;


    private Long id;
    private String day;
    //该模板总发送人数
    private int totalNum = 0;
    //该模板当天发送人数
    private int dayNum = 0;
    //该模板当天发送成功人数
    private int success = 0;
    //该模板当天发送中人数
    private int sending = 0;
    //该模板当天发送失败人数
    private int fail = 0;
    //存放userSize大小的模板最近用户的发送情况
    private Map<String, Integer> templateSendUserNum = new HashMap<>();
    //当天每个消息状态六个时间段的数据
    private Map<Integer, Integer> successList;
    private Map<Integer, Integer> sendingList;
    private Map<Integer, Integer> failList;
    //存放模板当天 发送成功 发送失败 发送中的次数
    private Map<Integer, Integer> templateStatus;
    //存放用户当天发送的所有模板情况 用于计算当前模板占比
    private Map<Long, Integer> templateCountOfDay = new HashMap<>();

    /**
     * 模板分析数据
     *
     * @param day
     * @param id
     * @return
     */

    @Override
    public TemplateEChartsData getTemplateEChartsData(String day, Long id) {
        //初始化数据
        initTemplateData(day, id);
        if (this.id == null || this.id == 0) {
            return null;
        }
        TemplateEChartsData.ShowPanelData showPanelData = buildShowPanelData();
        TemplateEChartsData.ShowUserEncodeData showUserEncodeData = buildShowUserEncodeData();
        TemplateEChartsData.ShowPieChartData showPieChartData = buildShowPieChartData();
        TemplateEChartsData.ShowBarChartData showBarChartData = buildShowBarChartData();
        TemplateEChartsData.ShowLineChartData showLineChartData = buildShowLineChartData();

        return TemplateEChartsData.builder().showPanelData(showPanelData).showUserEncodeData(showUserEncodeData)
                .showPieChartData(showPieChartData).showBarChartData(showBarChartData).showLineChartData(showLineChartData).build();
    }

    /**
     * 模板近七天发送情况线形图
     *
     * @return
     */
    private TemplateEChartsData.ShowLineChartData buildShowLineChartData() {
        // 获取前七天日期
        List<String> previousSevenDays = getPreviousSevenDays(day);
        TemplateEChartsData.ShowLineChartData.XAxis xAxis = TemplateEChartsData.ShowLineChartData.XAxis.builder().data(previousSevenDays).build();
        //存放模板七天的发送情况
        List<Map<Integer, Integer>> channelMapList = new ArrayList<>();
        for (int i = 0; i < previousSevenDays.size(); i++) {
            String key = RedisKeyUtil.getTemplateCountOfDayRedisKey(id, previousSevenDays.get(i));
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
        List<Integer> successs = new ArrayList<>();
        List<Integer> sendings = new ArrayList<>();
        List<Integer> fails = new ArrayList<>();
        for (int i = 0; i < channelMapList.size(); i++) {
            //遍历每一天的情况
            successs.add(channelMapList.get(i) == null ? 0 : channelMapList.get(i).get(MSG_SUCCESS));
            sendings.add(channelMapList.get(i) == null ? 0 : channelMapList.get(i).get(MSG_SENDING));
            fails.add(channelMapList.get(i) == null ? 0 : channelMapList.get(i).get(MSG_FAIL));
        }
        TemplateEChartsData.ShowLineChartData.Series.Data data = TemplateEChartsData.ShowLineChartData.Series.Data.builder()
                .successList(successs).sendingList(sendings).failList(fails).build();

        return TemplateEChartsData.ShowLineChartData.builder().xAxis(xAxis)
                .series(TemplateEChartsData.ShowLineChartData.Series.builder().data(data).build()).build();
    }


    /**
     * 消息当天各时间段发送情况
     *
     * @return
     */
    private TemplateEChartsData.ShowBarChartData buildShowBarChartData() {
        List<String> data = new ArrayList<>();
        data.add("00:00~03:59");
        data.add("04:00~07:59");
        data.add("08:00~11:59");
        data.add("12:00~15:59");
        data.add("16:00~19:59");
        data.add("20:00~23:59");

        List<TemplateEChartsData.ShowBarChartData.Series> series = new ArrayList<>();
        series.add(buildSeries("发送成功", successList));
        series.add(buildSeries("发送中", sendingList));
        series.add(buildSeries("发送失败", failList));
        TemplateEChartsData.ShowBarChartData.XAxis xAxis = TemplateEChartsData.ShowBarChartData.XAxis.builder().data(data).build();

        return TemplateEChartsData.ShowBarChartData.builder().xAxis(xAxis).series(series).build();
    }


    private TemplateEChartsData.ShowBarChartData.Series buildSeries(String status, Map<Integer, Integer> map) {
        if (CollectionUtil.isEmpty(map)) {
            map = createMap();
        }
        List<Integer> data = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            data.add(entry.getValue());
        }
        return TemplateEChartsData.ShowBarChartData.Series.builder()
                .name(status).data(data).build();

    }

    /**
     * 消息发送情况扇形图数据
     *
     * @return
     */
    private TemplateEChartsData.ShowPieChartData buildShowPieChartData() {
        return TemplateEChartsData.ShowPieChartData.builder().success(this.success).sending(this.sending).fail(this.fail).build();
    }

    /**
     * 当天发送的用户情况柱状图数据
     *
     * @return
     */
    private TemplateEChartsData.ShowUserEncodeData buildShowUserEncodeData() {
        List<List<String>> source = new ArrayList<>();
        List<String> first = new ArrayList<>();
        first.add("score");
        first.add("amount");
        first.add("product");
        source.add(first);
        for (Map.Entry<String, Integer> entry : this.templateSendUserNum.entrySet()) {
            List<String> element = new ArrayList<>();
            element.add("0");
            element.add(entry.getValue().toString());
            String s = entry.getKey().substring(0, Math.min(entry.getKey().length(), 14));
            String user;
            if (s.length() >= 14) {
                user = s + "..";
            } else {
                user = s;
            }
            element.add(user);
//            element.add(entry.getKey());
            source.add(element);
        }
        //清空原数据
        templateSendUserNum = new HashMap<>();
        return TemplateEChartsData.ShowUserEncodeData.builder().source(source).build();
    }

    /**
     * 模板总发送人数、模板当天发送人数、模板送达率、模板当天使用占比画板数据
     *
     * @return
     */
    private TemplateEChartsData.ShowPanelData buildShowPanelData() {
        Long userId = SecurityContextHolder.getUserId();
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(TEMPLATE_SEND_NUMBER_NAME + userId);
        if (entries.get(id.toString()) != null) {
            this.totalNum = Integer.parseInt(entries.get(id.toString()).toString());
        }
        //当天发送总人数
        int allOfDay = 0;
        for (Map.Entry<Long, Integer> entry : templateCountOfDay.entrySet()) {
            allOfDay += Integer.parseInt(entry.getValue().toString());
        }
        //送达率
        double deliverAbility = 0;
        //当天推送占比
        double pushPercentage = 0;
        if (allOfDay != 0) {
            pushPercentage = ((double) this.dayNum / allOfDay) * 100;
        }
        if (this.dayNum != 0) {
            deliverAbility = ((double) this.success / this.dayNum) * 100;
        }
        TemplateEChartsData.ShowPanelData showPanelData = TemplateEChartsData.ShowPanelData.builder().totalNum(this.totalNum)
                .dayNum(this.dayNum).deliverAbility(deliverAbility).pushPercentage(pushPercentage).build();
        //清空旧数据
        this.totalNum = 0;
        this.dayNum = 0;
        this.templateCountOfDay = new HashMap<>();
        return showPanelData;
    }

    public void initTemplateData(String day, Long id) {
        if ((StrUtil.isNotBlank(day) && !day.equals("null"))) {
            LocalDate date = LocalDate.parse(day);
            day = date.format(DateTimeFormatter.ofPattern(REDIS_DAY_KEY_FORMAT));
        } else {
            day = RedisKeyUtil.getCurrentDay();
        }
        Long userId = SecurityContextHolder.getUserId();
        this.day = day;
        if (id == null || id == 0) {
            //模板id为空 使用该用户最近的一个模板
            LambdaQueryWrapper<MessageTemplate> wrapper = Wrappers.lambdaQuery();
            wrapper.eq(MessageTemplate::getCreator, SecurityContextHolder.getUserName())
                    .orderByDesc(MessageTemplate::getUpdateTime).last("LIMIT 1");
            MessageTemplate template = messageTemplateService.getOne(wrapper);
            if (template == null) {
                return;
            }
            id = template.getId();
        }
        this.id = id;
        //获取当天消息key
        String redisKey = RedisKeyUtil.getMessageRedisKey(userId, day);
        //提前处理数据
        processingData(redisKey, id, day);

    }

    /**
     * 预处理数据
     *
     * @param redisKey
     * @param id
     * @param day
     */
    public void processingData(String redisKey, Long id, String day) {
        List<String> list = stringRedisTemplate.opsForList().range(redisKey, 0, -1);
        //清除缓存
        successList = createMap();
        sendingList = createMap();
        failList = createMap();

        this.success = 0;
        this.sending = 0;
        this.fail = 0;

        this.templateStatus = initTemplateStatus();
        if (CollectionUtil.isEmpty(list)) {
            return;
        }

        List<SendContent> sendContents = dataUtil.stringConvertSendContext(list);

        //存放当天下发人数
        AtomicReference<Integer> sendTotalOfDay = new AtomicReference<>();
        sendTotalOfDay.set(0);


        for (SendContent sendContent : sendContents) {
            sendContent.getSendTasks().forEach(sendTaskInfo -> {
                Long templateId = sendTaskInfo.getMessageTemplate().getId();
                //筛选出当天该模板的消息
                if (templateId.equals(id)) {
                    //统计指定日期发送成功、失败情况和发送中
                    if (MSG_SUCCESS.equals(sendTaskInfo.getMessageTemplate().getMsgStatus())) {
                        this.success += sendTaskInfo.getReceivers().size();
                        computeListData(successList, sendTaskInfo.getSendStartTime(), sendTaskInfo.getReceivers().size());
                        //统计模板的当天发送情况
                        templateStatus.put(MSG_SUCCESS, templateStatus.get(MSG_SUCCESS) + sendTaskInfo.getReceivers().size());
                    }
                    if (MSG_FAIL.equals(sendTaskInfo.getMessageTemplate().getMsgStatus())) {
                        this.fail += sendTaskInfo.getReceivers().size();
                        computeListData(failList, sendTaskInfo.getSendStartTime(), sendTaskInfo.getReceivers().size());
                        //统计模板的当天发送情况
                        templateStatus.put(MSG_FAIL, templateStatus.get(MSG_FAIL) + sendTaskInfo.getReceivers().size());
                    }
                    if (MSG_SENDING.equals(sendTaskInfo.getMessageTemplate().getMsgStatus())) {
                        this.sending += sendTaskInfo.getReceivers().size();
                        computeListData(sendingList, sendTaskInfo.getSendStartTime(), sendTaskInfo.getReceivers().size());
                        //统计模板的当天发送情况
                        templateStatus.put(MSG_SENDING, templateStatus.get(MSG_SENDING) + sendTaskInfo.getReceivers().size());
                    }
                    sendTotalOfDay.set(sendTotalOfDay.get() + sendTaskInfo.getReceivers().size());

                    for (String receiver : sendTaskInfo.getReceivers()) {
                        if (templateSendUserNum.containsKey(receiver)) {
                            templateSendUserNum.put(receiver, templateSendUserNum.get(receiver) + 1);
                        } else {
                            if (templateSendUserNum.size() < userSize) {
                                templateSendUserNum.put(receiver, 1);
                            }
                        }
                    }
                }
                //统计所有模板
                if (templateCountOfDay.containsKey(templateId)) {
                    templateCountOfDay.put(templateId, templateCountOfDay.get(templateId) + sendTaskInfo.getReceivers().size());
                } else {
                    templateCountOfDay.put(templateId, sendTaskInfo.getReceivers().size());
                }
            });

        }
        this.dayNum = sendTotalOfDay.get();
        recordingNumOfDy(day, id);
        recordingTemplateCount();
    }

    /**
     * 记录模板当天发送情况
     */
    private void recordingTemplateCount() {
        Set<Map.Entry<Integer, Integer>> entrySet = templateStatus.entrySet();
        //类型转换
        Map<String, String> convertedMap = new HashMap<>();
        for (Map.Entry<Integer, Integer> entry : entrySet) {
            convertedMap.put(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
        }
        stringRedisTemplate.opsForHash().putAll(RedisKeyUtil.getTemplateCountOfDayRedisKey(id, day), convertedMap);
    }


    /**
     * 统计当天所有模板消息被发送次数
     */
    private void recordingNumOfDy(String day, Long id) {
        Long userId = SecurityContextHolder.getUserId();
        Set<Map.Entry<Long, Integer>> entrySet = templateCountOfDay.entrySet();
        //类型转换
        Map<String, String> convertedMap = new HashMap<>();
        for (Map.Entry<Long, Integer> entry : entrySet) {
            convertedMap.put(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
        }
        stringRedisTemplate.opsForHash().putAll(TEMPLATE_SEND_NUMBER_DAY + userId + ":" + day, convertedMap);

    }

    /**
     * 初始化存放六个时间段数据的集合
     *
     * @return
     */
    private Map<Integer, Integer> createMap() {
        //按照顺序插入
        Map<Integer, Integer> map = new LinkedHashMap<>();
        for (int i = 0; i < 6; i++) {
            map.put(i, 0);
        }
        return map;
    }

    /**
     * 计算消息发送时间属于哪个时间段
     *
     * @param map
     * @param time
     */
    private void computeListData(Map<Integer, Integer> map, LocalDateTime time, int num) {
        int hour = time.getHour();
        int interval = hour / 4; // 将24小时分成6个4小时的时间段

        switch (interval) {
            case 0:
                map.put(0, map.get(0) + num);
                break;
            case 1:
                map.put(1, map.get(1) + num);
                break;
            case 2:
                map.put(2, map.get(2) + num);
                break;
            case 3:
                map.put(3, map.get(3) + num);
                break;
            case 4:
                map.put(4, map.get(4) + num);
                break;
            case 5:
                map.put(5, map.get(5) + num);
                break;
        }
    }

    /**
     * 获取前七天日期集合
     *
     * @param day
     * @return
     */
    public List<String> getPreviousSevenDays(String day) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(REDIS_DAY_KEY_FORMAT);
        LocalDate date = LocalDate.parse(day, formatter);

        List<String> previousSevenDays = new ArrayList<>();
        // 获取前六天日期
        for (int i = 6; i > 0; i--) {
            LocalDate previousDay = date.minusDays(i); // i 表示前i天
            String previousDayFormatted = previousDay.format(formatter);
            previousSevenDays.add(previousDayFormatted);
        }
        previousSevenDays.add(day);
        return previousSevenDays;
    }

    /**
     * 初始化templateStatus集合
     *
     * @return
     */
    private Map<Integer, Integer> initTemplateStatus() {
        Map<Integer, Integer> map = new HashMap<>();
        map.put(MSG_SUCCESS, 0);
        map.put(MSG_FAIL, 0);
        map.put(MSG_SENDING, 0);
        return map;
    }
}
