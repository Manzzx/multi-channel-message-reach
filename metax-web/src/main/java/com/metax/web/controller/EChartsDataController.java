package com.metax.web.controller;

import com.metax.common.core.web.controller.BaseController;
import com.metax.common.core.web.domain.AjaxResult;
import com.metax.system.api.domain.SysUser;
import com.metax.web.service.EChartsDataService;
import com.metax.web.service.ISysUserService;
import com.metax.web.service.TemplateEChartsDataService;
import com.metax.web.vo.echarts.IndexEChartsData;
import com.metax.web.vo.echarts.TemplateEChartsData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ECharts图数据控制层
 *
 * @Author: hanabi
 * @DateTime: 2023/11/2 21:16
 **/
@RestController
@RequestMapping("/eCharts")
public class EChartsDataController extends BaseController {

    @Autowired
    private EChartsDataService eChartsDataService;
    @Autowired
    private TemplateEChartsDataService templateEChartsDataService;
    @Autowired
    private ISysUserService userService;

    /**
     * 获取首页ECharts图数据
     * @return
     */
    @GetMapping("/index/{day}")
    public AjaxResult getIndexEChartsData(@PathVariable String day) {
        IndexEChartsData indexEChartsData = eChartsDataService.getIndexEChartsData(day,null);
        return AjaxResult.success(indexEChartsData);
    }

    /**
     * 获取模板分析ECharts图数据
     * @return
     */
    @GetMapping("/analyse/{day}/{id}")
    public AjaxResult getTemplateEChartsData(@PathVariable String day,@PathVariable Long id) {
        TemplateEChartsData templateEChartsData = templateEChartsDataService.getTemplateEChartsData(day, id);
        return AjaxResult.success(templateEChartsData);

    }

    @GetMapping("/users")
    public AjaxResult getUsers(){
        return AjaxResult.success(userService.lambdaQuery().eq(SysUser::getDelFlag,"0").list());
    }

    @GetMapping("/user/index/{day}/{user}")
    public AjaxResult getUserIndexEChartData(@PathVariable String day,@PathVariable Long user){
        return AjaxResult.success(eChartsDataService.getUserIndexEChartData(day,user));
    }

}
