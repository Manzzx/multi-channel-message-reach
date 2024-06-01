package com.metax.web.controller;

import com.metax.common.core.constant.HttpStatus;
import com.metax.common.core.web.controller.BaseController;
import com.metax.common.core.web.domain.AjaxResult;
import com.metax.common.core.web.page.TableDataInfo;
import com.metax.web.vo.ReceiverRecordsPage;
import com.metax.web.vo.SendTaskInfoVoPage;
import com.metax.web.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 消息数据查询controller
 *
 * @Author: hanabi
 * @DateTime: 2023/10/3 21:24
 **/
@RestController
@RequestMapping("/data")
public class DataController extends BaseController {

    @Autowired
    private IDataService dataService;

    /**
     * 获取当前用户某一天的所有发送记录
     *
     * @param pageNum
     * @param pageSize
     * @param sendMessageKey
     * @return
     */
    @GetMapping("/sendTaskInfo")
    public TableDataInfo sendTaskInfoList(int pageNum, int pageSize, String sendMessageKey) {
        SendTaskInfoVoPage page = dataService.getCurrentDayData(pageNum, pageSize, sendMessageKey, null);
        dataService.clearThreadData();
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setRows(page.getSendTaskInfoVos());
        rspData.setMsg("查询成功");
        rspData.setTotal(page.getTotal());

        return rspData;
    }

    /**
     * 获取某用户某一天的所有发送记录
     *
     * @param pageNum
     * @param pageSize
     * @param sendMessageKey
     * @return
     */
    @GetMapping("/sendTaskInfo/user")
    public TableDataInfo userSendTaskInfoList(int pageNum, int pageSize, String sendMessageKey, Long user) {
        SendTaskInfoVoPage page = dataService.getUserCurrentDayData(pageNum, pageSize, sendMessageKey, user);
        dataService.clearThreadData();
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setRows(page.getSendTaskInfoVos());
        rspData.setMsg("查询成功");
        rspData.setTotal(page.getTotal());

        return rspData;
    }

    /**
     * 清空指定用户指定日期所有数据
     *
     * @param date
     * @return
     */
    @DeleteMapping("/clear/{date}/{user}")
    public AjaxResult clear(@PathVariable String date,@PathVariable Long user) {
        return toAjax(dataService.clear(date,user));
    }

    @GetMapping("cronTaskCords/list")
    public TableDataInfo cronTaskCordsList(int pageNum, int pageSize, String messageTemplateId) {
        return dataService.cronTaskCordsList(pageNum, pageSize, messageTemplateId);
    }

    /**
     * 获取当前接受者某一天的所有发送记录
     *
     * @param pageNum
     * @param pageSize
     * @param sendMessageKey
     * @return
     */
    @GetMapping("/receiverRecords")
    public TableDataInfo receiverRecords(int pageNum, int pageSize, String receiver, String sendMessageKey) {
        ReceiverRecordsPage page = dataService.getReceiverByDay(pageNum, pageSize, receiver, sendMessageKey);
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setRows(page.getReceiverRecords());
        rspData.setMsg("查询成功");
        rspData.setTotal(page.getTotal());

        return rspData;
    }

}
