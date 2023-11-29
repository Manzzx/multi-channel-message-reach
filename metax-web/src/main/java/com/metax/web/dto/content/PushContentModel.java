package com.metax.web.dto.content;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * push通知栏消息模型
 * https://docs.getui.com/getui/server/rest_v2/common_args/?id=doc-title-6
 *
 * @Author: hanabi
 * @DateTime: 2023/10/28 23:40
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PushContentModel extends ContentModel {

    /**
     * 通知消息标题，长度 ≤ 50字
     */
    private String title;

    /**
     * 通知消息内容，长度 ≤ 256字
     */
    private String body;

    /**
     * 设置通知渠道重要性（可以控制响铃，震动，浮动，闪灯等等）
     * android8.0以下
     * 0，1，2:无声音，无振动，不浮动
     * 3:有声音，无振动，不浮动
     * 4:有声音，有振动，有浮动
     * android8.0以上
     * 0：无声音，无振动，不显示；
     * 1：无声音，无振动，锁屏不显示，通知栏中被折叠显示，导航栏无logo;
     * 2：无声音，无振动，锁屏和通知栏中都显示，通知不唤醒屏幕;
     * 3：有声音，无振动，锁屏和通知栏中都显示，通知唤醒屏幕;
     * 4：有声音，有振动，亮屏下通知悬浮展示，锁屏通知以默认形式展示且唤醒屏幕;
     */
    private String channelLevel;

    /**
     * intent：打开应用内特定页面，
     * url：打开网页地址，
     * payload：自定义消息内容启动应用，
     * payload_custom：自定义消息内容不启动应用，
     * startapp：打开应用首页，
     * none：纯通知，无后续动作
     */
    private String clickType;

    /**
     * click_type为url时必填
     * <p>
     * 点击通知栏消息时，唤起系统默认浏览器打开此链接。必须填写可访问的链接，url长度 ≤ 1024字
     * 示例：https://www.getui.com
     */
    private String url;

    /**
     * click_type为intent时必填
     * <p>
     * 点击通知打开应用特定页面，长度 ≤ 4096字;
     * 示例：intent://com.getui.push/detail?#Intent;scheme=gtpushscheme;launchFlags=0x4000000;
     * package=com.getui.demo;component=com.getui.demo/
     * com.getui.demo.DemoActivity;S.payload=payloadStr;end
     */
    private String intent;

    /**
     * click_type为payload/payload_custom时必填
     * <p>
     * 点击通知时，附加自定义透传消息，长度 ≤ 3072字
     */
    private String payload;
}
