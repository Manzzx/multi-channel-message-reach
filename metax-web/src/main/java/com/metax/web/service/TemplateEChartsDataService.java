package com.metax.web.service;

import com.metax.web.vo.echarts.TemplateEChartsData;

/**
 * @Author: hanabi
 * @DateTime: 2023/11/8 11:20
 **/
public interface TemplateEChartsDataService {

    public TemplateEChartsData getTemplateEChartsData(String day, Long id);

    void clearThreadData();
}
