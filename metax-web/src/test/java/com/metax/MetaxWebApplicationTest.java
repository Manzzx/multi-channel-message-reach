package com.metax;

import com.metax.web.domain.MessageTemplate;
import com.metax.web.domain.aliyun.AlibabaCloudSmsConfig;
import com.metax.web.domain.aliyun.QueryAlibabaCloudSMSReceiptParam;
import com.metax.web.service.IMessageTemplateService;
import com.metax.web.util.CsvFileUtil;
import com.metax.web.util.aliyun.AlibabaCloudSMSReceiptPullUtilsV2;
import com.metax.web.dto.SendForm;
import com.metax.web.util.tencent.TencentCloudSMSReceiptPullByPhoneUtils;
import com.metax.web.xxljob.util.XxlJobUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class MetaxWebApplicationTest {

    @Autowired
    private XxlJobUtil xxlJobUtil;
    @Autowired
    private IMessageTemplateService messageTemplateService;
    @Autowired
    private CsvFileUtil csvUtil;

    @Test
    void testGetGroup() {
        System.out.println(xxlJobUtil.saveGroup());
//        System.out.println(xxlJobUtil.getGroupId());
    }

    @Test
    void testCsv() throws IOException {
        MessageTemplate messageTemplate = messageTemplateService.getById(54);
        SendForm sendForm = csvUtil.readCsvBuildSendFom(messageTemplate,1L);
        System.out.println(sendForm);
    }

    @Test
    void testAliSMSPull() throws Exception {
        AlibabaCloudSMSReceiptPullUtilsV2.pull(new AlibabaCloudSmsConfig("cn-hangzhou","","","","","AlibabaCloudService"),
                new QueryAlibabaCloudSMSReceiptParam("",null, "20231031",10L,1L));
    }

    @Test
    void testTencentSMSPull() {
//        TencentCloudSMSReceiptPullByPhoneUtils.pull();
    }

    @Test
    void testDate() {
    }

    @Test
    void testTime() {

    }
}
