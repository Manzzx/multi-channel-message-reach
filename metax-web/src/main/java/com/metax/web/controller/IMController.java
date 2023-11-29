package com.metax.web.controller;

import com.alibaba.fastjson2.JSON;
import com.metax.common.core.context.SecurityContextHolder;
import com.metax.common.core.web.controller.BaseController;
import com.metax.common.core.web.domain.AjaxResult;
import com.metax.system.api.domain.SysUser;
import com.metax.web.service.ISysUserService;
import com.metax.web.util.RedisUtil;
import com.metax.web.webSocket.entity.MessageEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

/**
 * 即时聊天controller
 *
 * @Author: hanabi
 * @DateTime: 2023/11/15 17:52
 **/
@RestController
@RequestMapping("/im")
public class IMController extends BaseController {

    @Autowired
    private ISysUserService userService;
    @Autowired
    private RedisUtil redisUtil;

    @GetMapping("/getFriends")
    public List<SysUser> getFriends() {
        return userService.lambdaQuery().eq(SysUser::getDelFlag,"0").list();
    }

    @GetMapping("/pullMsg/{from}/{to}")
    public List<MessageEntity> pullMsg(@PathVariable Long from, @PathVariable Long to) {
        String key = LongStream.of(from, to)
                .sorted()
                .mapToObj(String::valueOf)
                .collect(Collectors.joining("-"));
        List<String> list = redisUtil.get(key);
        return list.stream().map(s->JSON.parseObject(s,MessageEntity.class)).collect(Collectors.toList());
    }
}