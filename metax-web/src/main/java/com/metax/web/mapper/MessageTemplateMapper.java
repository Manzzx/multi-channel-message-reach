package com.metax.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.metax.web.domain.MessageTemplate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 消息模板Mapper接口
 * 
 * @author hanabi
 * @date 2023-09-08
 */
@Mapper
public interface MessageTemplateMapper extends BaseMapper<MessageTemplate>
{
}
