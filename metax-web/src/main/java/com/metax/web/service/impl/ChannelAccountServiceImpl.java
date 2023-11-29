package com.metax.web.service.impl;

import java.util.List;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.metax.common.core.context.SecurityContextHolder;
import com.metax.web.domain.ChannelAccount;
import com.metax.web.dto.ChannelAccountDto;
import com.metax.web.mapper.ChannelAccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.metax.web.service.IChannelAccountService;

/**
 * 渠道账号Service业务层处理
 * 
 * @author hanabi
 * @date 2023-09-08
 */
@Service
public class ChannelAccountServiceImpl extends ServiceImpl<ChannelAccountMapper, ChannelAccount> implements IChannelAccountService
{
    @Autowired
    private ChannelAccountMapper channelAccountMapper;

    /**
     * 全部查询或条件查询
     * @param channelAccountDto
     * @return
     */
    @Override
    public IPage<ChannelAccount> getList(ChannelAccountDto channelAccountDto) {
        Integer pageNum = channelAccountDto.getPageNum();
        Integer pageSize = channelAccountDto.getPageSize();
        QueryWrapper<ChannelAccount> queryWrapper = new QueryWrapper<>();
        //Dto转原对象
        ChannelAccount channelAccount = BeanUtil.copyProperties(channelAccountDto, ChannelAccount.class);
        //bean转map
        queryWrapper.allEq(BeanUtil.beanToMap(channelAccount),false);
        queryWrapper.orderByDesc("updated");
        queryWrapper.eq("creator", SecurityContextHolder.getUserName());

        return page(new Page<>(pageNum, pageSize), queryWrapper);

    }
}
