package com.sx.dw.wealth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 *
 */

public class RechargeReceiver extends BroadcastReceiver {

    private PayCallback callback;

    public RechargeReceiver(PayCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        callback.paySuccess();
    }

    interface PayCallback{
        void paySuccess();
    }
}
