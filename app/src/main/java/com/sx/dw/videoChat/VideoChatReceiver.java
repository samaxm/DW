package com.sx.dw.videoChat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.apkfuns.logutils.LogUtils;

import static com.sx.dw.core.GlobalData.inChatRoom;

/**
 * 视频呼入广播接收器
 */
public class VideoChatReceiver extends BroadcastReceiver {

    private CallInCallback callback;

    public VideoChatReceiver(CallInCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String from = intent.getStringExtra("from");
        String type = intent.getStringExtra("type");
        LogUtils.d("type = " + type);
        if("video".equals(type)&&inChatRoom){
            callback.callIn(from);
        }
    }

    public interface CallInCallback {
        void callIn(String from);
    }
}
