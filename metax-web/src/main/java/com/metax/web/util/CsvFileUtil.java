package com.metax.web.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.text.csv.CsvData;
import cn.hutool.core.text.csv.CsvReadConfig;
import cn.hutool.core.text.csv.CsvRow;
import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.metax.common.core.web.domain.AjaxResult;
import com.metax.web.domain.MessageTemplate;
import com.metax.web.dto.SendForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

import static com.metax.common.core.constant.MetaxDataConstants.CRON_FILE_RECEIVER;
import static com.metax.common.core.constant.MetaxDataConstants.SEPARATOR;

/**
 * csv工具类
 * @Author: hanabi
 */
@Component
public class CsvFileUtil {

    @Autowired
    private ContentHolderUtil contentHolderUtil;

    /**
     * 读取csv人群文件
     * @param messageTemplate
     * @param sender
     * @return
     */
    public SendForm readCsvBuildSendFom(MessageTemplate messageTemplate,Long sender) {

        //获取变量名集合
        List<String> varNames = contentHolderUtil.getVariables(messageTemplate);
        SendForm sendForm = new SendForm();

        try {
            cn.hutool.core.text.csv.CsvReader reader = CsvUtil.getReader(new CsvReadConfig().setContainsHeader(true));
            CsvData data = reader.read(ResourceUtil.getReader(messageTemplate.getCronCrowdPath(), CharsetUtil.CHARSET_UTF_8));
            StringBuilder receivers = new StringBuilder();
            List<JSONObject> variables = new ArrayList<>();
            List<CsvRow> rows = data.getRows();
            if (CollectionUtil.isEmpty(rows)){
                sendForm.setIsNeedBreak(true);
                sendForm.setResponse(AjaxResult.error("消息:"+messageTemplate.getName()+" 人群文件不能为空"));
                return sendForm;
            }
            for (CsvRow row : rows) {
                //获取每一行表头列属性名和列值的映射
                Map<String, String> fieldMap = row.getFieldMap();
                //拼接接受者
                receivers.append(fieldMap.get(CRON_FILE_RECEIVER)).append(SEPARATOR);
                JSONObject jsonObject = new JSONObject();
                for (String varName : varNames) {
                    jsonObject.set(varName, fieldMap.get(varName));
                }
                variables.add(jsonObject);
            }

            //移除最后一个逗号
            receivers.deleteCharAt(receivers.lastIndexOf(SEPARATOR));
            sendForm.setReceivers(String.valueOf(receivers));
            if (CollectionUtil.isNotEmpty(varNames)) {
                //如果存在占位符
                sendForm.setVariables(JSONUtil.toJsonStr(variables));
                sendForm.setIsExitVariables(varNames.size());
            } else {
                sendForm.setVariables(StrUtil.EMPTY);
                sendForm.setIsExitVariables(0);
            }
            sendForm.setMessageTemplateId(messageTemplate.getId());
            sendForm.setSendChannel(messageTemplate.getSendChannel());
            sendForm.setSender(sender);
        }catch (Exception e){
            sendForm.setIsNeedBreak(true);
            sendForm.setResponse(AjaxResult.error("csv文件读取异常:"+e.getMessage()));
            return sendForm;
        }
        return sendForm;
    }
}
