package com.metax.common.core.constant;

import static com.metax.common.core.constant.MetaxDataConstants.APPLICATION_NAME;

/**
 * @Author: hanabi
 * @DateTime: 2023/10/28 19:12
 **/
public class GeTuiConstants {

    /**
     * 个推应用token metax:ge_tui_token:accountId
     */
    public static final String GE_TUI_TOKEN_KEY  = APPLICATION_NAME + "ge_tui_token:";
    /**
     * 让用户的个推token在redis中23小时过期
     */
    public static final long EXPIRE_TIME = 82800000;

    public static final String CLICK_TYPE_URL = "url";

    public static final String CLICK_TYPE_INTENT = "intent";

    public static final String CLICK_TYPE_PAYLOAD = "payload";

    public static final String CLICK_TYPE_PAYLOAD_CUSTOM = "payload_custom";

    public static final String CLICK_TYPE_STARTAPP = "startapp";

    public static final String CLICK_TYPE_NONE = "none";
}
