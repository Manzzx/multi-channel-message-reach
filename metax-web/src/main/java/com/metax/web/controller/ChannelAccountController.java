package com.metax.web.controller;

import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.pagehelper.PageInfo;
import com.metax.common.core.constant.HttpStatus;
import com.metax.common.core.context.SecurityContextHolder;
import com.metax.common.log.annotation.Log;
import com.metax.common.log.enums.BusinessType;
import com.metax.web.domain.ChannelAccount;
import com.metax.web.domain.MessageTemplate;
import com.metax.web.dto.ChannelAccountDto;
import com.metax.web.vo.ChannelAccountVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.metax.common.security.annotation.RequiresPermissions;
import com.metax.web.service.IChannelAccountService;
import com.metax.common.core.web.controller.BaseController;
import com.metax.common.core.web.domain.AjaxResult;
import com.metax.common.core.utils.poi.ExcelUtil;
import com.metax.common.core.web.page.TableDataInfo;

/**
 * 渠道账号Controller
 *
 * @author hanabi
 * @date 2023-09-08
 */
@RestController
@RequestMapping("/channel_account")
public class ChannelAccountController extends BaseController {
    @Autowired
    private IChannelAccountService channelAccountService;

    /**
     * 查询渠道账号列表
     */
    @RequiresPermissions("web:channel_account:list")
    @GetMapping("/list")
    public TableDataInfo list(ChannelAccountDto channelAccountDto) {
        IPage<ChannelAccount> page = channelAccountService.getList(channelAccountDto);
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setRows(page.getRecords());
        rspData.setMsg("查询成功");
        rspData.setTotal(page.getTotal());
        return rspData;
    }

    /**
     * 导出渠道账号列表
     */
    @RequiresPermissions("web:channel_account:export")
    @Log(title = "渠道账号", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ChannelAccount channelAccount) {
        List<ChannelAccount> list = channelAccountService.lambdaQuery().eq(ChannelAccount::getCreator, SecurityContextHolder.getUserName()).list();
        ExcelUtil<ChannelAccount> util = new ExcelUtil<ChannelAccount>(ChannelAccount.class);
        util.exportExcel(response, list, "渠道账号数据");
    }

    /**
     * 获取当前用户所有渠道账号
     */
    @RequiresPermissions("web:channel_account:query")
    @GetMapping(value = "/ChannelAccount/{sendChannel}")
    public AjaxResult getChannelAccounts(@PathVariable("sendChannel") Integer sendChannel) {
        List<ChannelAccount> channelAccounts = channelAccountService.lambdaQuery()
                .eq(ChannelAccount::getSendChannel, sendChannel)
                .eq(ChannelAccount::getCreator,SecurityContextHolder.getUserName())
                .list();
        return success(channelAccounts);
    }

    /**
     * 获取渠道账号详细信息
     */
    @RequiresPermissions("web:channel_account:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        ChannelAccount channelAccount = channelAccountService.getById(id);
        ChannelAccountVo channelAccountVo = BeanUtil.copyProperties(channelAccount, ChannelAccountVo.class);
        return success(channelAccountVo);
    }

    /**
     * 新增渠道账号
     */
    @RequiresPermissions("web:channel_account:add")
    @Log(title = "渠道账号", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ChannelAccount channelAccount) {
        channelAccount.setCreator(StrUtil.isBlank(channelAccount.getCreator()) ? SecurityContextHolder.getUserName() :channelAccount.getCreator());
        return toAjax(channelAccountService.save(channelAccount));
    }

    /**
     * 修改渠道账号
     */
    @RequiresPermissions("web:channel_account:edit")
    @Log(title = "渠道账号", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ChannelAccount channelAccount) {
        return toAjax(channelAccountService.updateById(channelAccount));
    }

    /**
     * 删除渠道账号
     */
    @RequiresPermissions("web:channel_account:remove")
    @Log(title = "渠道账号", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(channelAccountService.removeByIds(Arrays.asList(ids)));
    }
}
