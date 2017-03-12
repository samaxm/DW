package com.sx.dw.accountAndSecurity.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @Description: TODO 添加类的描述
 * @author: fanjie
 * @date: 2016/12/8 18:47
 */

public class VipPayReceiver extends BroadcastReceiver {

    private VipPayCallback callback;

    public VipPayReceiver(VipPayCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        callback.paySuccess();
    }

    interface VipPayCallback{
        void paySuccess();
    }
}
