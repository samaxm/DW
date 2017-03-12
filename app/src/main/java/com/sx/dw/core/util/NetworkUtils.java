package com.sx.dw.core.util;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

import com.sx.dw.core.DWApplication;

/**
 * @Description: TODO 添加类的描述
 * @author: fanjie
 * @date: 2016/10/29 14:02
 */

public class NetworkUtils {

    /**
     * 检测网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetWorkConnected(Context context) {
        if (context != null) {
            ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = conMan.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 获取网络状态
     *
     * @param context
     * @return
     */
    public static NetworkInfo getActiveNetwork(Context context) {
        if (context == null)
            return null;
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conMan == null)
            return null;
        NetworkInfo aActiveInfo = conMan.getActiveNetworkInfo(); // 获取活动网络连接信息
        return aActiveInfo;
    }

    /**
     * WiFi是否开启
     *
     * @param context
     * @return
     */
    public static boolean isWifiOpen(Context context) {
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo.State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        if (wifi == NetworkInfo.State.CONNECTED) {
            return true;
        }
        return false;
    }

    /**
     * 数据流量是否开启(显示3G网络连接状态)
     *
     * @param context
     * @return
     */
    public static boolean isMobileOpen(Context context) {
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo.State mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        if (mobile == NetworkInfo.State.CONNECTED) {
            return true;
        }
        return false;
    }

    /**
     * 打开网络设置
     *
     * @param context
     */
    public static void openNetSetting(Context context) {
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo.State mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        NetworkInfo.State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();

        if (mobile == NetworkInfo.State.CONNECTED || mobile == NetworkInfo.State.CONNECTING)
            return;
        if (wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING)
            return;
        context.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
    }

    public static boolean isNetWorkConnected() {
        return isNetWorkConnected(DWApplication.getInstance());
    }
}
