package com.metax.web.dto;

import com.metax.web.domain.ChannelAccount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 接受前端参数对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChannelAccountDto extends ChannelAccount {

    private Integer pageNum;

    private Integer pageSize;
}
