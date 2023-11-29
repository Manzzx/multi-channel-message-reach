package com.metax.web.xxljob.task;

import cn.hutool.core.util.StrUtil;
import com.metax.web.handler.CronTaskHandler;
import com.metax.web.util.DataUtil;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.*;

import static com.metax.common.core.constant.MetaxDataConstants.*;
import static com.metax.common.core.constant.QueueConstants.BIG_QUEUE_SIZE;

/**
 * 定时任务入口
 *
 * @Author: hanabi
 */
@Component
@Slf4j
public class CronTask {

    @Resource
    private ThreadPoolExecutor xxlDtpExecutor;
    @Autowired
    private CronTaskHandler cronTaskHandler;
    @Autowired
    private DataUtil dataUtil;
    // 监听线程池
    private static final ExecutorService LISTEN_EXECUTOR = Executors.newSingleThreadExecutor();

    // 创建一个阻塞队列 用来存放定时任务
    private final BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>(BIG_QUEUE_SIZE);

    @PostConstruct
    public void initQueue(){

        LISTEN_EXECUTOR.execute(() -> {
            while (true) {
                try {
                    xxlDtpExecutor.execute(taskQueue.take());
                } catch (Exception e) {
                    log.error("定时任务处理异常:{}", e.getMessage());
                }
            }
        });
    }

    @XxlJob("metaxJob")
    public void cronTaskExecutor() {
        String jobParam = XxlJobHelper.getJobParam();
        List<String> params = StrUtil.split(jobParam, SEPARATOR);
        dataUtil.recordCronTaskStatus(CRON_TASK_SCHEDULING, Long.valueOf(params.get(0)), Long.valueOf(params.get(1)), "启动中");

        try {
            taskQueue.put(() -> cronTaskHandler.Handler(Long.valueOf(params.get(0)), Long.valueOf(params.get(1))));
        } catch (Exception e) {
            log.error("定时任务提交异常:{}", e.getMessage());
        }
    }
}
