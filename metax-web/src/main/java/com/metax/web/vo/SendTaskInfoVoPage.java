package com.metax.web.vo;

import com.metax.web.vo.SendTaskInfoVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: hanabi
 * @DateTime: 2023/10/4 16:09
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendTaskInfoVoPage {

    private List<SendTaskInfoVo> sendTaskInfoVos;

    private long total;
}
