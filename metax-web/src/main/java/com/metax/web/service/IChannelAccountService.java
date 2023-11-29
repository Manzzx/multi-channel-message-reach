package com.metax.web.service;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.metax.web.domain.ChannelAccount;
import com.metax.web.dto.ChannelAccountDto;

/**
 * 渠道账号Service接口
 * 
 * @author hanabi
 * @date 2023-09-08
 */
public interface IChannelAccountService extends IService<ChannelAccount>
{
    IPage<ChannelAccount> getList(ChannelAccountDto channelAccountDto);
}
