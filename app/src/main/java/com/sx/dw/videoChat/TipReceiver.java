package com.sx.dw.videoChat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sx.dw.core.util.C;

/**
 * @Description: TODO 添加类的描述
 * @author: fanjie
 * @date: 2016/12/8 20:02
 */

public class TipReceiver extends BroadcastReceiver {
    private TipCallback callback;

    public TipReceiver(TipCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int i = intent.getIntExtra(C.IntentExtra.TIP_AMOUNT, -1);
        callback.getTip(i);
    }

    interface TipCallback{
        void getTip(int amount);
    }
}
