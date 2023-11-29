package com.metax.web.controller;

import com.metax.common.core.constant.HttpStatus;
import com.metax.common.core.web.controller.BaseController;
import com.metax.common.core.web.domain.AjaxResult;
import com.metax.common.core.web.page.TableDataInfo;
import com.metax.web.dto.QuerySmsRecordDto;
import com.metax.web.service.SmsService;
import com.metax.web.vo.SmsRecordPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: hanabi
 * @DateTime: 2023/10/30 23:05
 **/
@RestController
@RequestMapping("/sms")
public class SmsController extends BaseController {

    @Autowired
    private SmsService smsService;

    @GetMapping("/pushRecord")
    public TableDataInfo pushRecord(QuerySmsRecordDto queryParams){
        SmsRecordPage page = smsService.pushRecord(queryParams);
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setRows(page.getSmsRecords());
        rspData.setMsg("查询成功");
        rspData.setTotal(page.getTotal());
        return rspData;
    }

    @GetMapping("/recent")
    public TableDataInfo recentRecord(QuerySmsRecordDto queryParams){
        SmsRecordPage page = smsService.pushRecentRecord(queryParams);
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setRows(page.getSmsRecords());
        rspData.setMsg("查询成功");
        rspData.setTotal(page.getTotal());
        return rspData;
    }

    @DeleteMapping("/clear")
    public AjaxResult clear(){
        return toAjax(smsService.clear());
    }
}
