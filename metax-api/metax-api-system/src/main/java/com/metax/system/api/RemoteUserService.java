package com.metax.system.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import com.metax.common.core.constant.SecurityConstants;
import com.metax.common.core.constant.ServiceNameConstants;
import com.metax.common.core.domain.R;
import com.metax.system.api.domain.SysUser;
import com.metax.system.api.factory.RemoteUserFallbackFactory;
import com.metax.system.api.model.LoginUser;

/**
 * 用户服务
 * 
 * @author ruoyi
 */
@FeignClient(contextId = "remoteUserService", value = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteUserFallbackFactory.class)
public interface RemoteUserService
{
    /**
     * 通过用户名查询用户信息
     *
     * @param username 用户名
     * @param source 请求来源
     * @return 结果
     */
    @GetMapping("/user/info/{username}")
    public R<LoginUser> getUserInfo(@PathVariable("username") String username, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 注册用户信息
     *
     * @param sysUser 用户信息
     * @param source 请求来源
     * @return 结果
     */
    @PostMapping("/user/register")
    public R<Boolean> registerUserInfo(@RequestBody SysUser sysUser, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 注册默认角色 普通用户
     * @param userId
     * @param roleIds
     * @param source
     * @return
     */
//    @PutMapping("/user/authRole")
//    public R<Boolean> insertAuthRole(Long userId, Long[] roleIds,@RequestHeader(SecurityConstants.FROM_SOURCE) String source);
}
