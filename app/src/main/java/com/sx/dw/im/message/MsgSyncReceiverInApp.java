package com.sx.dw.im.message;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.apkfuns.logutils.LogUtils;
import com.sx.dw.im.entity.ChatMsg;
import com.sx.dw.im.ui.MsgHelper;

import static com.sx.dw.core.util.C.IntentExtra.CHAT_MSG;

public class MsgSyncReceiverInApp extends BroadcastReceiver {
    public MsgSyncReceiverInApp() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.d("onReceive()");
        MsgHelper helper = new MsgHelper(context);
        ChatMsg msg = (ChatMsg) intent.getSerializableExtra(CHAT_MSG);
        msg.setUnreadMsg(true);
        msg.save();
        helper.sendMsgNotify(msg);

    }

}
