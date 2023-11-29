package com.metax.web.handler;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.metax.common.core.constant.MetaxDataConstants;
import com.metax.common.core.exception.ServiceException;
import com.metax.web.domain.MessageTemplate;
import com.metax.web.domain.content.SendContent;
import com.metax.web.domain.SendTaskInfo;
import com.metax.web.dto.content.EmailContentModel;
import com.metax.web.util.AccountUtil;
import com.metax.web.util.DataUtil;
import com.metax.web.util.FileUtil;
import com.metax.web.xxljob.domain.CronTaskCords;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.alibaba.fastjson2.JSON.parseObject;
import static com.metax.common.core.constant.MetaxDataConstants.*;
import static com.metax.common.core.web.domain.AjaxResult.MSG_TAG;

/**
 * 邮箱处理
 * @Author: hanabi
 */
@Service
@Slf4j
public class EmailHandler extends ChannelHandler {

    @Autowired
    private AccountUtil accountUtil;

    @Value("${metax.cacheFilePath}")
    private String path;

    @Autowired
    private DataUtil dataUtil;


    /**
     * 真正开始消费
     */
    @Override
    public void doHandler(SendTaskInfo sendTaskInfo) {
        String sendId = null;
        Long sendTaskId = sendTaskInfo.getSendTaskId();
        String sendMessageKey = sendTaskInfo.getSendMessageKey();

        try {
            EmailContentModel mailContent = JSON.parseObject(sendTaskInfo.getMessageTemplate().getMsgContent(), EmailContentModel.class);
            MailAccount mailAccount = accountUtil.getAccount(sendTaskInfo.getMessageTemplate().getSendAccount(), MailAccount.class);

            List<String> urls = StrUtil
                    .isNotBlank(mailContent.getUrl()) ? StrUtil.split(mailContent.getUrl(), MetaxDataConstants.SEPARATOR) : null;

            List<File> files = CollectionUtil.isNotEmpty(urls) ? FileUtil.getRemoteUrl2File(path, urls) : null;

            if (CollectionUtil.isNotEmpty(files)) {
                sendId = MailUtil.send(mailAccount, sendTaskInfo.getReceivers(), mailContent.getTitle(), mailContent.getContent(),
                        true, files.toArray(new File[files.size()]));
            } else {
                sendId = MailUtil.send(mailAccount, sendTaskInfo.getReceivers(), mailContent.getTitle(), mailContent.getContent(),
                        true);
            }
            dataUtil.confirmSend(sendId, sendTaskInfo.getMessageId(), sendMessageKey, sendTaskId,new Exception());
        } catch (Exception e) {
            dataUtil.confirmSend(sendId, sendTaskInfo.getMessageId(), sendMessageKey, sendTaskId,e);
            e.printStackTrace();
        }
    }

}
