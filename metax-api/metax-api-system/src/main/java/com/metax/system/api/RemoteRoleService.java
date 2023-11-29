package com.metax.system.api;

import com.metax.common.core.constant.SecurityConstants;
import com.metax.common.core.constant.ServiceNameConstants;
import com.metax.common.core.domain.R;
import com.metax.system.api.domain.SysRole;
import com.metax.system.api.domain.SysUser;
import com.metax.system.api.factory.RemoteRoleFallbackFactory;
import com.metax.system.api.factory.RemoteUserFallbackFactory;
import com.metax.system.api.model.LoginUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * 角色服务
 * 
 * @author ruoyi
 */
@FeignClient(contextId = "remoteRoleService", value = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteRoleFallbackFactory.class)
public interface RemoteRoleService
{
    /**
     * 通过用户名查询用户信息
     *
     * @param roleId 用户名
     * @param source 请求来源
     * @return 结果
     */
    @GetMapping("/role/{roleId}")
    public R<SysRole> getRoleInfo(@PathVariable("roleId") Long roleId, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

}
