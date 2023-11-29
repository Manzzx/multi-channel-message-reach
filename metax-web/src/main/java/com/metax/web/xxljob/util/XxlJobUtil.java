package com.metax.web.xxljob.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSON;
import com.metax.web.xxljob.domain.XxlJobGroup;
import com.metax.web.xxljob.domain.XxlJobInfo;
import com.xxl.job.core.biz.model.ReturnT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.metax.common.core.constant.MetaxDataConstants.XXL_GROUP_TYPE_AUTO;

/**
 * xxl接口封装工具类
 * @Author: hanabi
 */
@Component
@Slf4j
public class XxlJobUtil {

    /**
     * 调度中心地址
     */
    @Value("${xxl.job.admin.addresses}")
    private String adminAddresses;

    /**
     * 执行器名称
     */
    @Value("${xxl.job.executor.appname}")
    private String appname;

    @Value("${xxl.job.executor.jobHandlerName}")
    private String title;


    private static final String ADD_URL = "/jobinfo/add";
    private static final String UPDATE_URL = "/jobinfo/update";
    private static final String REMOVE_URL = "/jobinfo/remove";
    private static final String STOP_URL = "/jobinfo/stop";
    private static final String START_URL = "/jobinfo/start";
    private static final String GET_GROUP_ID = "/jobgroup/pageList";
    private static final String SAVE_GROUP_URL = "/jobgroup/save";


    /**
     * 获取执行器id
     *
     * @return 执行器id
     */
    public Integer getGroupId() {
        // 查询对应groupId:
        Map<String, Object> param = new HashMap<>();
        param.put("appname", appname);
        param.put("title", title);
        //根据执行器名称，获取执行器id
        String result = doPost(adminAddresses + GET_GROUP_ID, param);
        Map<String, Object> map = JSON.parseObject(result, Map.class);
        List<XxlJobGroup> data = (List<XxlJobGroup>) map.get("data");
        Integer id = CollectionUtil.isEmpty(data) ? null : JSON.parseObject(result).getJSONArray("data").getJSONObject(0).getInteger("id");
        //执行器不存在
        if (id == null) {
            String s = saveGroup();
            ReturnT returnT = JSON.parseObject(s, ReturnT.class);
            if (ReturnT.SUCCESS_CODE == returnT.getCode()) {
                //创建成功再次获取执行器id
                String r = doPost(adminAddresses + GET_GROUP_ID, param);
                Map<String, Object> map2 = JSON.parseObject(r, Map.class);
                List<XxlJobGroup> ids= (List<XxlJobGroup>) map2.get("data");
                id = CollectionUtil.isEmpty(ids) ? null : JSON.parseObject(r).getJSONArray("data").getJSONObject(0).getInteger("id");
                return id;
            }
        }
        return id;
    }

    /**
     * 新建任务
     *
     * @return {"code":200,"msg":null,"content":"3"} content:任务id
     */
    public String add(XxlJobInfo xxlJobInfo) {
        Map<String, Object> params = JSON.parseObject(JSON.toJSONString(xxlJobInfo), Map.class);
        //新建任务
        return doPost(adminAddresses + ADD_URL, params);
    }

    /**
     * 修改任务
     *
     * @return {"code":200,"msg":null,"content":null}
     */
    public String update(XxlJobInfo xxlJobInfo) {
        Map<String, Object> params = JSON.parseObject(JSON.toJSONString(xxlJobInfo), Map.class);
        xxlJobInfo.setJobGroup(getGroupId());
        return doPost(adminAddresses + UPDATE_URL, params);
    }

    /**
     * 删除任务
     *
     * @param id 任务id
     * @return {"code":200,"msg":null,"content":null}
     */
    public String remove(int id) {
        Map<String, Object> param = new HashMap<>();
        param.put("id", id);
        return doPost(adminAddresses + REMOVE_URL, param);
    }

    /**
     * 停止任务
     *
     * @param id 任务id
     * @return {"code":200,"msg":null,"content":null}
     */
    public String stop(int id) {
        Map<String, Object> param = new HashMap<>();
        param.put("id", id);
        return doPost(adminAddresses + STOP_URL, param);
    }

    /**
     * 启动任务
     *
     * @param id 任务id
     * @return {"code":200,"msg":null,"content":null}
     */
    public String start(int id) {
        Map<String, Object> param = new HashMap<>();
        param.put("id", id);
        return doPost(adminAddresses + START_URL, param);
    }

    /**
     * 创建执行器
     *
     * @return
     */
    public String saveGroup() {
        XxlJobGroup xxlJobGroup = XxlJobGroup.builder().appname(appname).title(title).addressType(XXL_GROUP_TYPE_AUTO).build();
        Map<String, Object> params = JSON.parseObject(JSON.toJSONString(xxlJobGroup), Map.class);
        return doPost(adminAddresses + SAVE_GROUP_URL, params);
    }

    /**
     * 调用任务调度中心接口
     *
     * @param url 调度中心地址
     * @return
     */
    public String doPost(String url, Map<String, Object> param) {
        //调用任务调度中心接口
        HttpResponse response = HttpRequest.post(url).form(param).execute();
        return response.body();
    }


}
