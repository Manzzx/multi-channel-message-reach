package com.metax.web.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.metax.common.core.context.SecurityContextHolder;
import com.metax.web.domain.MessageTemplate;
import com.metax.web.domain.dingding.DingDingRobotParam;
import com.metax.web.dto.MessageTemplateDto;
import com.metax.web.dto.content.DingDingRobotContentModel;
import com.metax.web.dto.content.FeiShuRobotContentModel;
import com.metax.web.dto.content.PushContentModel;
import com.metax.web.dto.content.WeChatServiceAccountContentModel;
import com.metax.web.mapper.MessageTemplateMapper;
import com.metax.web.util.DataUtil;
import com.metax.web.xxljob.service.XxlJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.metax.web.service.IMessageTemplateService;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.metax.common.core.constant.SendMessageTypeConstants.*;
import static com.metax.common.core.constant.GeTuiConstants.*;
import static com.metax.common.core.constant.GeTuiConstants.CLICK_TYPE_PAYLOAD_CUSTOM;
import static com.metax.common.core.constant.MetaxDataConstants.*;

/**
 * 消息模板Service业务层处理
 *
 * @author hanabi
 * @date 2023-09-08
 */
@Service
public class MessageTemplateServiceImpl extends ServiceImpl<MessageTemplateMapper, MessageTemplate> implements IMessageTemplateService {
    @Autowired
    private MessageTemplateMapper messageTemplateMapper;
    @Autowired
    private XxlJobService xxlJobService;
    @Autowired
    private DataUtil dataUtil;



    @Override
    public boolean add(MessageTemplate messageTemplate) {
        messageTemplate.setCreator(StrUtil.isBlank(messageTemplate.getCreator()) ? SecurityContextHolder.getUserName() : messageTemplate.getCreator());
        if (messageTemplate.getSendAccount() == null || messageTemplate.getSendAccount() == 0) {
            return false;
        }
        if (messageTemplate.getSendChannel() == null || messageTemplate.getSendChannel() == 0) {
            return false;
        }
        if (dingDongRobotPreCheck(messageTemplate)) {
            return false;
        }
        if (weChatServiceAccountPreCheck(messageTemplate)){
            return false;
        }
        if (pushPreCheck(messageTemplate)){
            return false;
        }
        if (feiShuRobotPreCheck(messageTemplate)){
            return false;
        }
        if (Objects.equals(messageTemplate.getPushType(), TIMING)) {
            //如果是定时
            messageTemplate.setMsgStatus(MSG_STOP);
        }
        return save(messageTemplate);
    }

    /**
     * 修改
     *
     * @param messageTemplate
     * @return
     */
    @Override
    public boolean edit(MessageTemplate messageTemplate) {
        if (dingDongRobotPreCheck(messageTemplate)) {
            return false;
        }
        return updateById(messageTemplate);
    }

    @Override
    public boolean delete(Long[] ids) {
        List<MessageTemplate> messageTemplates = messageTemplateMapper.selectBatchIds(Arrays.asList(ids));
        for (MessageTemplate messageTemplate : messageTemplates) {
            if (Objects.nonNull(messageTemplate.getCronTaskId())) {
                //如果定时任务id不为空(证明插入过xxl数据库)
                xxlJobService.remove(messageTemplate.getCronTaskId());
            }
            if (Objects.nonNull(messageTemplate.getCronCrowdPath())) {
                //同时删除文件
                FileUtil.del(messageTemplate.getCronCrowdPath());
            }
        }
        return removeByIds(Arrays.asList(ids));
    }


    /**
     * 查询消息模板列表
     *
     * @param messageTemplate 消息模板
     * @return 消息模板
     */
    @Override
    public IPage<MessageTemplate> selectMessageTemplateList(MessageTemplateDto messageTemplate, Long pageNum, Long pageSize) {
        LambdaQueryWrapper<MessageTemplate> lqw = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(messageTemplate.getName())) {
            lqw.like(MessageTemplate::getName, messageTemplate.getName());
        }
        if (StrUtil.isNotBlank(messageTemplate.getSendChannel())) {
            lqw.eq(MessageTemplate::getSendChannel,dataUtil.channelMappingToInteger().get(messageTemplate.getSendChannel()));
        }
        if (StrUtil.isNotBlank(messageTemplate.getCreateTime())){
            lqw.like(MessageTemplate::getCreateTime,messageTemplate.getCreateTime());
        }
        lqw.orderByDesc(MessageTemplate::getUpdateTime).eq(MessageTemplate::getCreator,SecurityContextHolder.getUserName());
        IPage<MessageTemplate> iPage = new Page<>(pageNum, pageSize);
        page(iPage, lqw);
        return iPage;
    }


    /**
     * 查询审核消息模板列表
     *
     * @param messageTemplate 消息模板
     * @return 消息模板
     */
    @Override
    public IPage<MessageTemplate> selectAuditMessageTemplateList(MessageTemplateDto messageTemplate, Long pageNum, Long pageSize) {
        LambdaQueryWrapper<MessageTemplate> lqw = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(messageTemplate.getName())) {
            lqw.like(MessageTemplate::getName, messageTemplate.getName());
        }
        if (StrUtil.isNotBlank(messageTemplate.getCreator())) {
            lqw.like(MessageTemplate::getCreator, messageTemplate.getCreator());
        }
        if (StrUtil.isNotBlank(messageTemplate.getCreateTime())){
            lqw.like(MessageTemplate::getCreateTime,messageTemplate.getCreateTime());
        }
        lqw.orderByDesc(MessageTemplate::getUpdateTime);
        IPage<MessageTemplate> iPage = new Page<>(pageNum, pageSize);
        page(iPage, lqw);
        return iPage;
    }

    @Override
    public boolean updateAudit(Long id, Integer status) {
        return lambdaUpdate().eq(MessageTemplate::getId, id).set(MessageTemplate::getAuditStatus, status).update();
    }

    @Override
    public List<MessageTemplate> getMessages() {
        return lambdaQuery().eq(MessageTemplate::getCreator, SecurityContextHolder.getUserName())
                .orderByDesc(MessageTemplate::getUpdateTime).list();
    }

    /**
     * 钉钉机器人后端校验
     *
     * @param messageTemplate
     * @return true 需要拦截 false 放行
     */
    public boolean dingDongRobotPreCheck(MessageTemplate messageTemplate) {
        Integer sendChannel = messageTemplate.getSendChannel();
        if (!DING_DING_ROBOT.equals(sendChannel)) {
            return false;
        }
        DingDingRobotContentModel contentModel = JSON.parseObject(messageTemplate.getMsgContent(), DingDingRobotContentModel.class);
        if (TEXT.equals(contentModel.getSendType())) {
            //文本类型
            JSONObject jsonObject = JSON.parseObject(contentModel.getContent());
            DingDingRobotParam.Text text = jsonObject.toJavaObject(DingDingRobotParam.Text.class);
            if (StrUtil.isBlank(text.getContent())) {
                return true;
            }
        }
        if (LINK.equals(contentModel.getSendType())) {
            //link类型
            JSONObject jsonObject = JSON.parseObject(contentModel.getContent());
            DingDingRobotParam.Link link = jsonObject.toJavaObject(DingDingRobotParam.Link.class);
            if (StrUtil.isBlank(link.getTitle()) || StrUtil.isBlank(link.getText()) || StrUtil.isBlank(link.getMessageUrl()) || StrUtil.isBlank(link.getPicUrl())) {
                return true;
            }
        }
        if (MARKDOWN.equals(contentModel.getSendType())) {
            //markdown类型
            JSONObject jsonObject = JSON.parseObject(contentModel.getContent());
            DingDingRobotParam.Markdown markdown = jsonObject.toJavaObject(DingDingRobotParam.Markdown.class);
            if (StrUtil.isBlank(markdown.getTitle()) || StrUtil.isBlank(markdown.getText())) {
                return true;
            }
        }
        if (ACTION_CARD.equals(contentModel.getSendType())) {
            //actionCard类型
            JSONObject jsonObject = JSON.parseObject(contentModel.getContent());
            DingDingRobotParam.ActionCard actionCard = jsonObject.toJavaObject(DingDingRobotParam.ActionCard.class);
            if (StrUtil.isBlank(actionCard.getTitle()) || StrUtil.isBlank(actionCard.getText()) || CollectionUtil.isEmpty(actionCard.getBtns()) || StrUtil.isBlank(actionCard.getBtnOrientation())) {
                return true;
            }
        }
        if (FEED_CARD.equals(contentModel.getSendType())) {
            //feedCard类型
            String content = contentModel.getContent();
            JSONObject jsonObject = JSON.parseObject(content);
            DingDingRobotParam.FeedCard feedCard = jsonObject.toJavaObject(DingDingRobotParam.FeedCard.class);
            List<DingDingRobotParam.FeedCard.Links> links = feedCard.getLinks();
            return CollectionUtil.isEmpty(links);

        }
        return false;
    }

    /**
     * 微信服务号后端校验
     * @param messageTemplate
     * @return true 需要拦截 false 放行
     */
    public boolean weChatServiceAccountPreCheck(MessageTemplate messageTemplate){
        Integer sendChannel = messageTemplate.getSendChannel();
        if (!WECHAT_SERVICE_ACCOUNT.equals(sendChannel)) {
            return false;
        }
        WeChatServiceAccountContentModel contentModel = JSON.parseObject(messageTemplate.getMsgContent(), WeChatServiceAccountContentModel.class);
        if (StrUtil.isBlank(contentModel.getTemplateId())){
            return true;
        }
        if (StrUtil.isBlank(contentModel.getUrl())){
            return true;
        }
        if (contentModel.getLinkType() == null || contentModel.getLinkType() ==0){
            return true;
        }
        return false;
    }

    /**
     * APP通知栏后端校验
     * @param messageTemplate
     * @return true 需要拦截 false 放行
     */
    public boolean pushPreCheck(MessageTemplate messageTemplate){
        Integer sendChannel = messageTemplate.getSendChannel();
        if (!PUSH.equals(sendChannel)) {
            return false;
        }
        PushContentModel contentModel = JSON.parseObject(messageTemplate.getMsgContent(), PushContentModel.class);
        String clickType = contentModel.getClickType();
        if (StrUtil.isBlank(contentModel.getChannelLevel())){
            return true;
        }
        if (CLICK_TYPE_URL.equals(clickType)) {
            if (StrUtil.isBlank(contentModel.getUrl())){
                return true;
            }
        }
        if (CLICK_TYPE_INTENT.equals(clickType)) {
            if (StrUtil.isBlank(contentModel.getIntent())){
                return true;
            }
        }
        if (CLICK_TYPE_PAYLOAD.equals(clickType)) {
            if (StrUtil.isBlank(contentModel.getPayload())){
                return true;
            }
        }
        if (CLICK_TYPE_PAYLOAD_CUSTOM.equals(clickType)) {
            if (StrUtil.isBlank(contentModel.getPayload())){
                return true;
            }
        }
        return false;
    }

    /**
     * 飞书机器人后端校验
     * @param messageTemplate
     * @return
     */
    public boolean feiShuRobotPreCheck(MessageTemplate messageTemplate){
        FeiShuRobotContentModel contentModel = JSON.parseObject(messageTemplate.getMsgContent(), FeiShuRobotContentModel.class);
        if (TEXT.equals(contentModel.getMsgType())){
            if (StrUtil.isBlank(contentModel.getText().getContent())){
                return true;
            }
        }
        return false;
    }
}
