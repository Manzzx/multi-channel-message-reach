package com.metax.web.service;

import com.metax.web.vo.echarts.IndexEChartsData;
import com.metax.web.vo.echarts.TemplateEChartsData;

/**
 * @Author: hanabi
 * @DateTime: 2023/11/2 21:19
 **/
public interface EChartsDataService {

    public IndexEChartsData getIndexEChartsData(String day,Long user);

    IndexEChartsData getUserIndexEChartData(String day, Long user);
}
