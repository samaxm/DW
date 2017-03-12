package com.sx.dw.im.message;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sx.dw.im.entity.ChatMsg;

import static com.sx.dw.core.util.C.IntentExtra.CHAT_MSG;

public class MsgSyncReceiverInChat extends BroadcastReceiver {
    private MsgSyncListener listener;
    public MsgSyncReceiverInChat(MsgSyncListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ChatMsg msg = (ChatMsg) intent.getSerializableExtra(CHAT_MSG);
        listener.onMsgGet(msg);
    }
}
