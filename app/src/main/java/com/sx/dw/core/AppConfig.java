/**
 *
 */
package com.sx.dw.core;

/**
 * 全局变量配置
 */
public class AppConfig {

    /**
     * 时间相关
     **/
    // 显示段子的时间
    public static final int INTERVAL_JOKE_SHOW = 1000 * 12;
    // 匹配的间隔时间
    public static final int INTERVAL_MATCH = 1000 * 15;
    // 发送在线统计的间隔时间
    public static final int INTERVAL_PING = 1000 * 60 * 10;

    // 是否为云端
    public static final boolean IS_CLOUD = true;

    // 是否调试模式
    public static final boolean IS_DEBUG = false;
}
