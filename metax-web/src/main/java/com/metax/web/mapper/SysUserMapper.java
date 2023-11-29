package com.metax.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.metax.system.api.domain.SysUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
}
