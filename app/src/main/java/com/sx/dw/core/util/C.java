/**
 *
 */
package com.sx.dw.core.util;

/**
 * @ClassName: C
 * @Description: 全局常量配置
 * @author: Jackchen
 * @date: 2016年8月17日 上午9:24:21
 */
public final class C {

    public static final String WECHAT_APP_ID = "wxab5989e542c3703d";
//    public static final String WECHAT_APP_ID = "wx506552ee38f03198";
    public static final String WECHAT_SIGN = "bb369828427afbfa4633ef0923b52ee9";

//    public static final String QQ_APP_ID = "101352168";
//    public static final String QQ_SIGN = "a27f4c1a28be93cdd3e5855495eaacbd";
    public static final String QQ_APP_ID = "101363809";
    public static final String QQ_SIGN = "019add0d9327d78f2613061275c40c11";


    public static final String STAR="腕";


    public static final String AVOS_CLOUD_KEY = "YkvjrFQUCHhPMg6tUo9xlHGu-gzGzoHsz";
    public static final String AVOS_CLOUD_CODE = "UQ8WphzIbk0Q7OuTjWTxWaWL";
    public static final String LOG_TAG = "dw_log";

    public static final String BROADCAST_ACTION_GET_MSG = "com.sx.dw.BROADCAST_ACTION_GET_MSG";
    public static final String BROADCAST_ACTION_RECHARGE = "com.sx.dw.BROADCAST_ACTION_RECHARGE";
    public static final String BROADCAST_ACTION_VIP_PAY = "com.sx.dw.BROADCAST_ACTION_VIP_PAY";
    public static final String BROADCAST_ACTION_TIP = "com.sx.dw.BROADCAST_ACTION_TIP";

    public static final int BROADCAST_PRIORITY_IN_CHAT = 9000;
    public static final int BROADCAST_PRIORITY_IN_ACTIVITY = 5000;
    public static final String SP_FILE_NAME_VIP = "SP_FILE_NAME_VIP";
    public static final String SP_KEY_VIP = "SP_KEY_VIP";


    public static class IntentExtra{
        public static final String CHAT_MSG = "chatMsg";
        public static final String LINKMAN = "userEntity";
        public static final String REQUEST_CODE = "requestCode";
        public static final String ACCOUNT = "dwId";
        public static final String TOKEN = "token";
        public static final String VIP_CODE = "vipCode";
        public static final String DW_ID = "dwID";
        public static final String TIP_AMOUNT = "tipAmount";
    }

    public static class RequestCode{
//        验证和密码相关
        public static final int BIND_PHONE = 100;
        public static final int LOGIN_BIND_PHONE = 101;
        public static final int RESET_PASSWORD = 102;
        public static final int FORGET_PASSWORD = 103;
        public static final int SET_PAY_PSD_AND_WITHDRAW = 104;
        public static final int SET_PAY_PSD = 105;
        public static final int BIND_AND_SET_PAY_PSD = 106;


//        社交网络相关
        public static final int LIKE_LIST_TO_CHAT = 201;
        public static final int LIKE_LIST_TO_USER_INFO = 204;
        public static final int CHAT_LIST_TO_CHAT = 202;
        public static final int MSG_LIST_TO_USER_INFO = 205;
        public static final int MSG_NOTIFY_TO_CHAT = 203;
        public static final int SEARCH_TO_CHAT = 200;

        public static final int USER_CENTER_TO_SETTING_WIFI = 301;
        public static final int USER_CENTER_TO_VIDEO_CHAT = 302;

        public static final int SETTING_INFO = 303;
        public static final int RESET_INFO = 304;
        public static final int SELECT_IMAGE = 305;
        public static final int SEARCH_TO_INFO = 306;


        public static final int JUST_BIND_WECHAT = 401;
        public static final int WEALTH_TO_WITHDRAW = 402;

        public static final int LOADING_TO_VIDEO_CHAT = 505;
        public static final int LOADING_TO_SETTING_WIFI = 506;

        public static final int CHECK_VIP_CODE_TO_PAY = 601;
        public static final int VIP_REGISTER = 602;
        public static final int SET_WORTH = 701;
    }

    public class ResultCode {
        public static final int WXPAY_SUCCESS = 1001;
    }
}
