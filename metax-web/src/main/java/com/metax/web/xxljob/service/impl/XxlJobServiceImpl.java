package com.metax.web.xxljob.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.metax.common.core.context.SecurityContextHolder;
import com.metax.common.core.exception.ServiceException;
import com.metax.common.core.web.domain.AjaxResult;
import com.metax.web.domain.MessageTemplate;
import com.metax.web.mapper.MessageTemplateMapper;
import com.metax.web.util.DataUtil;
import com.metax.web.xxljob.domain.CronTaskCords;
import com.metax.web.xxljob.domain.XxlJobInfo;
import com.metax.web.xxljob.enums.ExecutorRouteStrategyEnum;
import com.metax.web.xxljob.enums.MisfireStrategyEnum;
import com.metax.web.xxljob.enums.ScheduleTypeEnum;
import com.metax.web.xxljob.service.XxlJobService;
import com.metax.web.xxljob.util.XxlJobUtil;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.enums.ExecutorBlockStrategyEnum;
import com.xxl.job.core.glue.GlueTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;

import static com.metax.common.core.constant.MetaxDataConstants.*;
import static com.metax.common.core.web.domain.AjaxResult.DATA_TAG;

@Service
@Slf4j
public class XxlJobServiceImpl implements XxlJobService {

    @Autowired
    private XxlJobUtil xxlJobUtil;
    @Autowired
    private MessageTemplateMapper messageTemplateMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private DataUtil dataUtil;

    @Value("${xxl.job.executor.jobHandlerName}")
    private String jobName;


    /**
     * 保存xxlJob任务
     *
     * @param messageTemplate
     * @return
     */
    @Override
    public AjaxResult save(MessageTemplate messageTemplate) {
        XxlJobInfo xxlJobInfo = buildXxlJobInfo(messageTemplate);
        String result = xxlJobUtil.add(xxlJobInfo);
        ReturnT returnT = JSON.parseObject(result, ReturnT.class);
        if (ReturnT.SUCCESS_CODE != returnT.getCode()) {
            throw new ServiceException("定时消息任务:" + messageTemplate.getName() + "启动失败!");
        }
        //保存任务到调度中心成功 返回id
        Integer cronTaskId = Integer.valueOf((String) returnT.getContent());
        return AjaxResult.success(cronTaskId);
    }

    /**
     * 构建xxlJobInfo
     *
     * @param messageTemplate
     * @return
     */
    @Override
    public XxlJobInfo buildXxlJobInfo(MessageTemplate messageTemplate) {

        String pushTime = messageTemplate.getExpectPushTime();
        String pushTimeTemp = pushTime;
        //0 或者 不指定时间都是立即执行
        if (pushTime == null || PUSH_NOW.equals(pushTime)) {
            pushTime = DateUtil.format(DateUtil.offsetSecond(new Date(), DELAY_TIME), CRON_FORMAT);
        }

        boolean validExpression = CronExpression.isValidExpression(pushTime);
        if (!validExpression && !PUSH_NOW.equals(pushTimeTemp)) {
            throw new ServiceException("定时消息任务:" + messageTemplate.getName() + " cron表达式存在错误 请修改!");
        }

        XxlJobInfo xxlJobInfo = XxlJobInfo.builder()
                .jobGroup(xxlJobUtil.getGroupId()).jobDesc(messageTemplate.getName())
                .author(messageTemplate.getCreator())
                .scheduleConf(pushTime)
                .scheduleType(ScheduleTypeEnum.CRON.name())
                .misfireStrategy(MisfireStrategyEnum.DO_NOTHING.name())
                .executorRouteStrategy(ExecutorRouteStrategyEnum.CONSISTENT_HASH.name())
                .executorHandler(jobName)
                //将模板id和发送方id作为任务参数
                .executorParam(messageTemplate.getId()+SEPARATOR+SecurityContextHolder.getUserId())
                .executorBlockStrategy(ExecutorBlockStrategyEnum.SERIAL_EXECUTION.name())
                .executorTimeout(TIME_OUT)
                .executorFailRetryCount(RETRY_COUNT)
                .glueType(GlueTypeEnum.BEAN.name())
                .triggerStatus(TRIGGER_STATUS_FALSE)
                .glueRemark(StrUtil.EMPTY)
                .glueSource(StrUtil.EMPTY)
                .alarmEmail(NOTING_EMAIL)
                .childJobId(StrUtil.EMPTY).build();

        if (Objects.nonNull(messageTemplate.getCronTaskId())) {
            xxlJobInfo.setId(messageTemplate.getCronTaskId());
        }
        return xxlJobInfo;
    }

    /**
     * 启动任务
     *
     * @param id
     * @return
     */
    public AjaxResult start(Long id) {
        Long sender = SecurityContextHolder.getUserId();
        MessageTemplate messageTemplate = messageTemplateMapper.selectById(id);
        if (!Objects.nonNull(messageTemplate.getCronTaskId())) {
            //不存在cronTaskId
            AjaxResult result = save(messageTemplate);
            Integer cronTaskId = (Integer) result.get(DATA_TAG);
            messageTemplate.setCronTaskId(cronTaskId);
        } else {
            //存在cronTask 需要修改xxl再执行
            update(messageTemplate);
        }
        //xxl会修改triggerStatus为1
        ReturnT returnT = JSON.parseObject(xxlJobUtil.start(messageTemplate.getCronTaskId()), ReturnT.class);
        if (ReturnT.SUCCESS_CODE != returnT.getCode()) {
            throw new ServiceException("定时消息任务:" + messageTemplate.getName() + " 启动失败!原因:" + returnT.getMsg());
        }
        messageTemplate.setMsgStatus(MSG_START);
        //锁定定时模板资源
        messageTemplate.setCurrentId(sender);
        //更新消息数据库状态
        synchronized (this) {
            messageTemplateMapper.updateById(messageTemplate);
        }
        //记录定时任务状态
        CronTaskCords taskCords = CronTaskCords.builder().expectPushTime(messageTemplate.getExpectPushTime())
                .status(CRON_TASK_STARTING).startTime(LocalDateTime.now()).log("任务开始").messageTemplateId(id)
                .sender(sender).sendChannel(dataUtil.channelMapping().get(messageTemplate.getSendChannel())).build();
        stringRedisTemplate.opsForValue().set(CRON_TASK_STATUS_KEY + sender + ":" + id, JSON.toJSONString(taskCords));
        return AjaxResult.success();
    }

    /**
     * 暂停任务
     *
     * @param id
     * @return
     */
    @Override
    public AjaxResult stop(Long id) {
        MessageTemplate messageTemplate = messageTemplateMapper.selectById(id);
        ReturnT returnT = JSON.parseObject(xxlJobUtil.stop(messageTemplate.getCronTaskId()), ReturnT.class);
        if (ReturnT.SUCCESS_CODE != returnT.getCode()) {
            throw new ServiceException("定时消息任务:" + messageTemplate.getName() + "暂停失败!" + returnT.getMsg());
        }
        messageTemplate.setMsgStatus(MSG_STOP);
        //释放定时模板权限
        messageTemplate.setCurrentId(-1L);
        synchronized (this) {
            messageTemplateMapper.updateById(messageTemplate);
        }
        dataUtil.recordCronTaskStatus(CRON_TASK_STOP, messageTemplate.getId(),SecurityContextHolder.getUserId(), null);
        return AjaxResult.success();
    }

    /**
     * 修改
     *
     * @param messageTemplate
     * @return
     */
    @Override
    public AjaxResult update(MessageTemplate messageTemplate) {
        XxlJobInfo xxlJobInfo = buildXxlJobInfo(messageTemplate);
        ReturnT returnT = JSON.parseObject(xxlJobUtil.update(xxlJobInfo), ReturnT.class);
        if (ReturnT.SUCCESS_CODE != returnT.getCode()) {
            throw new ServiceException("定时消息任务:" + messageTemplate.getName() + "修改失败!" + returnT.getMsg());
        }
        return AjaxResult.success();
    }

    /**
     * 删除定时任务
     *
     * @param id
     * @return
     */
    @Override
    public AjaxResult remove(Integer id) {
        ReturnT returnT = JSON.parseObject(xxlJobUtil.remove(id), ReturnT.class);
        if (ReturnT.SUCCESS_CODE != returnT.getCode()) {
            throw new ServiceException("定时消息任务删除失败!" + returnT.getMsg());
        }
        return AjaxResult.success();
    }


}
