package com.metax.web.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.metax.common.core.constant.HttpStatus;
import com.metax.common.core.context.SecurityContextHolder;
import com.metax.common.log.enums.BusinessType;

import com.metax.common.log.annotation.Log;
import com.metax.web.domain.MessageTemplate;
import com.metax.web.dto.MessageTemplateDto;
import com.metax.web.service.SendMessageService;
import com.metax.web.util.ContentHolderUtil;
import com.metax.web.util.DataUtil;
import com.metax.web.vo.MessageTemplateVo;
import com.metax.web.dto.SendForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.metax.common.security.annotation.RequiresPermissions;
import com.metax.web.service.IMessageTemplateService;
import com.metax.common.core.web.controller.BaseController;
import com.metax.common.core.web.domain.AjaxResult;
import com.metax.common.core.utils.poi.ExcelUtil;
import com.metax.common.core.web.page.TableDataInfo;

import static com.metax.common.core.constant.MetaxDataConstants.REAL_TIME;

/**
 * 消息模板Controller
 *
 * @author hanabi
 * @date 2023-09-08
 */
@RestController
@RequestMapping("/message_template")
public class MessageTemplateController extends BaseController {
    @Autowired
    private IMessageTemplateService messageTemplateService;
    @Autowired
    private SendMessageService sendMessageService;
    @Autowired
    private DataUtil dataUtil;
    @Autowired
    private ContentHolderUtil contentHolderUtil;

    /**
     * 查询消息模板列表
     */
    @RequiresPermissions("web:message_template:list")
    @GetMapping("/list")
    public TableDataInfo list(MessageTemplateDto messageTemplate,Long pageNum, Long pageSize) {
        IPage<MessageTemplate> iPage = messageTemplateService.selectMessageTemplateList(messageTemplate, pageNum, pageSize);
        List<MessageTemplateDto> messageTemplateDtos = getTemplateDtos(iPage.getRecords());
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setRows(messageTemplateDtos);
        rspData.setMsg("查询成功");
        rspData.setTotal(iPage.getTotal());
        return rspData;
    }

    /**
     * 查询消息模板列表
     */
    @RequiresPermissions("web:message_template:list")
    @GetMapping("/audit/list")
    public TableDataInfo auditList(MessageTemplateDto messageTemplate,Long pageNum, Long pageSize) {
        IPage<MessageTemplate> iPage = messageTemplateService.selectAuditMessageTemplateList(messageTemplate, pageNum, pageSize);
        List<MessageTemplateDto> messageTemplateDtos = getTemplateDtos(iPage.getRecords());
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setRows(messageTemplateDtos);
        rspData.setMsg("查询成功");
        rspData.setTotal(iPage.getTotal());
        return rspData;
    }

    /**
     * 查询所有占位符名称集合
     */
    @RequiresPermissions("web:message_template:list")
    @GetMapping("/variables/{id}")
    public TableDataInfo listVariables(@PathVariable Long id) {
        MessageTemplate messageTemplate = messageTemplateService.getById(id);
        List<String> variables = contentHolderUtil.getVariables(messageTemplate);
        return getDataTable(variables);
    }

    /**
     * 导出消息模板列表
     */
    @RequiresPermissions("web:message_template:export")
    @Log(title = "消息模板", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, MessageTemplate messageTemplate) {
        List<MessageTemplate> list = messageTemplateService.lambdaQuery().eq(MessageTemplate::getCreator, SecurityContextHolder.getUserName()).list();
        ExcelUtil<MessageTemplate> util = new ExcelUtil<MessageTemplate>(MessageTemplate.class);
        util.exportExcel(response, list, "消息模板数据");
    }

    /**
     * 获取消息模板详细信息
     */
    @RequiresPermissions("web:message_template:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        MessageTemplateVo messageTemplateVo = BeanUtil.copyProperties(messageTemplateService.getById(id), MessageTemplateVo.class);
        return success(messageTemplateVo);
    }

    /**
     * 新增消息模板
     */
    @RequiresPermissions("web:message_template:add")
    @Log(title = "消息模板", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody MessageTemplate messageTemplate) {
        return messageTemplateService.add(messageTemplate) ? AjaxResult.success():AjaxResult.error("存在未通过校验字段");
    }

    /**
     * 修改消息模板
     */
    @RequiresPermissions("web:message_template:edit")
    @Log(title = "消息模板", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody MessageTemplate messageTemplate) {
        return toAjax(messageTemplateService.edit(messageTemplate));
    }

    /**
     * 删除消息模板
     */
    @RequiresPermissions("web:message_template:remove")
    @Log(title = "消息模板", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {

        return toAjax(messageTemplateService.delete(ids));
    }

    /**
     * 发送消息
     * @param sendForm
     * @return
     */
    @RequiresPermissions("web:message_template:send")
    @Log(title = "消息模板", businessType = BusinessType.SEND)
    @PostMapping("/send")
    public AjaxResult send(@RequestBody SendForm sendForm) {
        return sendMessageService.send(sendForm);
    }


    /**
     * 启动消息
     * @param id
     * @return
     */
    @RequiresPermissions("web:message_template:start")
    @Log(title = "消息模板", businessType = BusinessType.START)
    @GetMapping("/start/{id}")
    public AjaxResult start(@PathVariable Long id) {
        return sendMessageService.start(id);
    }

    /**
     * 暂停消息
     * @param id
     * @return
     */
    @RequiresPermissions("web:message_template:stop")
    @Log(title = "消息模板", businessType = BusinessType.STOP)
    @GetMapping("/stop/{id}")
    public AjaxResult stop(@PathVariable Long id) {
        return sendMessageService.stop(id);
    }

    /**
     * 修改模板审核状态
     * @param id
     * @return
     */
    @RequiresPermissions("web:message_template:audit")
    @Log(title = "消息模板", businessType = BusinessType.OTHER)
    @GetMapping("/audit/{id}/{status}")
    public AjaxResult updateAudit(@PathVariable Long id,@PathVariable Integer status) {
        return  toAjax(messageTemplateService.updateAudit(id,status));
    }

    /**
     * 查询消息模板列表
     */
    @RequiresPermissions("web:message_template:list")
    @GetMapping("/list/temp")
    public AjaxResult list() {
        return AjaxResult.success(messageTemplateService.getMessages());
    }


    private List<MessageTemplateDto> getTemplateDtos(List<MessageTemplate> list) {
        return list.stream().map(m -> {
            MessageTemplateDto mDto = BeanUtil.copyProperties(m, MessageTemplateDto.class, "pushType", "sendChannel","msgStatus");
            mDto.setPushType(m.getPushType() == REAL_TIME ? "实时" : "定时");
            mDto.setMsgStatus(dataUtil.statusMapping.get(m.getMsgStatus()));
            Map<Integer, String> channelMapping = dataUtil.channelMapping();
            mDto.setSendChannel(channelMapping.get(m.getSendChannel()));
            return mDto;
        }).collect(Collectors.toList());
    }
}
