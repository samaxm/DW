package com.sx.dw.im.ui;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.apkfuns.logutils.LogUtils;
import com.sx.dw.R;
import com.sx.dw.core.DWApplication;
import com.sx.dw.core.GlobalData;
import com.sx.dw.im.entity.Chat;
import com.sx.dw.im.entity.ChatMsg;
import com.sx.dw.im.entity.LinkMan;

import static com.sx.dw.core.util.C.IntentExtra.LINKMAN;
import static com.sx.dw.core.util.C.IntentExtra.REQUEST_CODE;
import static com.sx.dw.core.util.C.RequestCode.MSG_NOTIFY_TO_CHAT;

/**
 * @Description: TODO 添加类的描述
 * @author: fanjie
 * @date: 2016/10/1 15:10
 */

public class MsgHelper {
    private Context context;

    public MsgHelper(Context context) {
        this.context = context;
    }

    public void sendMsgNotify(ChatMsg msg) {
        LogUtils.d(msg);
        Chat chat = GlobalData.chatMap.get(msg.getFromID());
        LinkMan linkMan = chat.getLinkMan();

        Uri ringUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(DWApplication.getInstance())
                .setSmallIcon(R.drawable.ic_notification_msg)
                .setContentTitle(linkMan.getName())
                .setContentText(msg.getText())
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setWhen(msg.getTime())
                .setTicker(msg.getTicker())
                .setAutoCancel(true)
                .setSound(ringUri);

        Intent resultIntent = new Intent(context, ChatActivity.class);
        resultIntent.putExtra(LINKMAN, linkMan);
        resultIntent.putExtra(REQUEST_CODE, MSG_NOTIFY_TO_CHAT);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(ChatActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        int code = Integer.parseInt(chat.getId() + "");
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(code, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(code, builder.build());
    }
}
