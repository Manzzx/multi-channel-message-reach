package com.metax.web.dto.content;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 钉钉 自定义机器人
 * https://open.dingtalk.com/document/group/custom-robot-access
 *
 * @Author: hanabi
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DingDingRobotContentModel extends ContentModel {

    /**
     * 发送类型
     */
    private String sendType;

    /**
     * 钉钉机器人：【文本消息】内容，【markdown消息】内容，【ActionCard消息】内容，【FeedCard消息】内容，【link消息】内容
     */
    private String content;

}
