package com.metax.web.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.metax.system.api.domain.SysUser;
import com.metax.web.mapper.SysUserMapper;
import com.metax.web.service.ISysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ISysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {
}
