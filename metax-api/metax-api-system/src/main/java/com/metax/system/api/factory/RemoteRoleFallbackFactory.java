package com.metax.system.api.factory;

import com.metax.common.core.domain.R;
import com.metax.system.api.RemoteRoleService;
import com.metax.system.api.RemoteUserService;
import com.metax.system.api.domain.SysRole;
import com.metax.system.api.domain.SysUser;
import com.metax.system.api.model.LoginUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * 角色服务降级处理
 * 
 * @author hanabi
 */
@Component
public class RemoteRoleFallbackFactory implements FallbackFactory<RemoteRoleService>
{
    private static final Logger log = LoggerFactory.getLogger(RemoteRoleFallbackFactory.class);

    @Override
    public RemoteRoleService create(Throwable throwable)
    {
        log.error("角色服务调用失败:{}", throwable.getMessage());
        return new RemoteRoleService()
        {
            @Override
            public R<SysRole> getRoleInfo(Long roleId, String source)
            {
                return R.fail("获取角色失败:" + throwable.getMessage());
            }

        };
    }
}
