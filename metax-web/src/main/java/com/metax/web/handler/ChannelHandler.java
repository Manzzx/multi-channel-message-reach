package com.metax.web.handler;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.metax.web.domain.SendTaskInfo;
import com.metax.web.dto.content.SmsContentModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 渠道handler抽象类
 * @Author: hanabi
 */
public abstract class ChannelHandler {

   public void handler(SendTaskInfo sendTaskInfo){
      doHandler(sendTaskInfo);
   };

   abstract void doHandler(SendTaskInfo sendTaskInfo);

   /**
    * 长链接转短链 功能未实现
    */
   public JSONObject getSmsContent(SmsContentModel smsContentModel) {
      JSONObject jsonObject = JSON.parseObject(smsContentModel.getContent());
      if (StrUtil.isNotBlank(smsContentModel.getUrl())) {
         List<String> urlVariables = getUrlVariables(smsContentModel.getUrl());
         if (CollectionUtil.isNotEmpty(urlVariables)){
            //不为空 使用了链接占位符
            for (String urlVariable : urlVariables) {
               //这是取出来的原长链接
               String url = (String) jsonObject.get(urlVariable);
               //这里进行长链转短链 未实现
            }
         }
         return jsonObject;
      } else {
         return jsonObject;
      }
   }

   public List<String> getUrlVariables(String url){
      List<String> urls = new ArrayList<>();
      Pattern pattern = Pattern.compile("\\$\\{([^}]+)\\}");
      Matcher matcher = pattern.matcher(url);

      while (matcher.find()){
         String key = matcher.group(1); // 获取占位符键，不包括 "${" 和 "}"
         urls.add(key);
      }

      return urls;
   }
}
