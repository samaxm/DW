package com.sx.dw.im.message;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.alibaba.fastjson.JSON;
import com.apkfuns.logutils.LogUtils;
import com.sx.dw.accountAndSecurity.ui.LoginActivity;
import com.sx.dw.core.DWApplication;
import com.sx.dw.core.GlobalData;
import com.sx.dw.core.network.DwCallback;
import com.sx.dw.core.network.DwRetrofit;
import com.sx.dw.core.network.EntityHead;
import com.sx.dw.core.util.C;
import com.sx.dw.core.util.ToastUtil;
import com.sx.dw.im.entity.Chat;
import com.sx.dw.im.entity.ChatMsg;
import com.sx.dw.im.entity.JPushLikeEntity;
import com.sx.dw.im.entity.LinkMan;
import com.sx.dw.social.UserInfoApi;
import com.sx.dw.videoChat.JPushTipEntity;
import com.umeng.analytics.MobclickAgent;

import java.util.List;
import java.util.Random;

import cn.jpush.android.api.JPushInterface;
import retrofit2.Call;

import static com.sx.dw.core.GlobalData.accountInfo;
import static com.sx.dw.core.util.C.IntentExtra.CHAT_MSG;

/**
 * 无设计的临时方案，借极光推送接收like消息，复制消息同步服务的消息处理流程，通过全局Content发送广播
 */

public class JPushReceiverInApp extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {

        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
//            当我们应用没有任何activity在栈顶时启动应用
            if (!hasAnyOurActivityOnTop(context)) {
                Intent intent2 = new Intent(context, LoginActivity.class);
                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent2);
            }
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            LogUtils.v(bundle);
            String title = bundle.getString(JPushInterface.EXTRA_TITLE);
            String type = bundle.getString(JPushInterface.EXTRA_CONTENT_TYPE);
            String string = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            LogUtils.d(string);
            if ("JSON".equals(type) && "NOTICE_LIKE".equals(title)) {
                JPushLikeEntity likeEntity = JSON.parseObject(string, JPushLikeEntity.class);
                initMsg(likeEntity);
            } else if ("JSON".equals(type) && "NOTICE_RECHARGE".equals(title)) {
                Intent i1 = new Intent(C.BROADCAST_ACTION_RECHARGE);
                DWApplication.getInstance().sendBroadcast(i1);
            } else if ("JSON".equals(type) && "NOTICE_VIP_REGISTER".equals(title)) {
                Intent i2 = new Intent(C.BROADCAST_ACTION_VIP_PAY);
                DWApplication.getInstance().sendBroadcast(i2);
            }else if ("JSON".equals(type) && "NOTICE_TIP".equals(title)) {
                JPushTipEntity tipEntity = JSON.parseObject(string, JPushTipEntity.class);
                if(tipEntity!=null) {
                    Intent i3 = new Intent(C.BROADCAST_ACTION_TIP);
                    i3.putExtra(C.IntentExtra.TIP_AMOUNT, tipEntity.getBody().getAmount());
                    // TODO: 2016/12/8 这里考虑下是不是用同步广播
                    DWApplication.getInstance().sendBroadcast(i3);
                }
            }
        }
    }

    private void initMsg(final JPushLikeEntity likeMsg) {
        ChatMsg likeChatMsg = new ChatMsg();
        likeChatMsg.setFromID(likeMsg.getBody().getLikeID());
        likeChatMsg.setToId(likeMsg.getBody().getBeLikedID());
        likeChatMsg.setTextMsgType(ChatMsg.TextMsgType.LIKE);
        likeChatMsg.setTime(likeMsg.getTime());
//        likeChatMsg.setMessageType(MessageProtos.ChatMessageType.TEXT);
        likeChatMsg.setText(likeMsg.getBody().getName() + "收藏了你");
        ToastUtil.showToast(likeMsg.getBody().getName() + "收藏了你");
        initChatMsg(likeChatMsg);
        new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(1000*60);
                } catch (InterruptedException e) {

                }
                ChatMsg getChatMsg = new ChatMsg();
                getChatMsg.setFromID(likeMsg.getBody().getLikeID());
                getChatMsg.setToId(likeMsg.getBody().getBeLikedID());
                getChatMsg.setTextMsgType(ChatMsg.TextMsgType.GET);
                getChatMsg.setTime(likeMsg.getTime()+1000*60);
                getChatMsg.setMid(System.currentTimeMillis());
                getChatMsg.setStatus(MessageProtos.ChargeStatus.NOMAL);

                getChatMsg.setText(getRandomText());
                initChatMsg(getChatMsg);
            }
        } .start();

    }

    private String getRandomText() {
        String[] likeMsgs = {"交个朋友吧", "一面之缘，让我很难忘", "哈喽哈喽"
                , "~(～￣▽￣)～", "在吗？", "还记得我吗？", "o(*￣▽￣*)o"};
        Random random = new Random();
        return likeMsgs[random.nextInt(likeMsgs.length)];
    }

    /**
     * 初始化消息，判断本地是否有会话，无则创建并储存；判断本地是否有联系人，无则发起同步网络请求
     *
     * @param msg 经过处理最终存储的消息
     */
    private void initChatMsg(ChatMsg msg) {
        msg.setUnreadMsg(true);
        if (accountInfo != null) {
            try {
                accountInfo.setWealth(Integer.parseInt(msg.getReceiverWealth()));
            } catch (Exception e) {
                MobclickAgent.reportError(DWApplication.getInstance(), "身家解析错误，估计\n" + e);
                LogUtils.e("身家解析错误，估计\n" + e);
            }
        }
        initChat(msg);
        initLinkMan(msg);
    }

    private void initChat(ChatMsg msg) {
        Chat chat = GlobalData.chatMap.get(msg.getFromID());
        if (chat == null) {
            chat = new Chat();
            chat.setLinkManId(msg.getFromID());
            chat.save();
            GlobalData.updateChats();
        }
    }

    private void initLinkMan(final ChatMsg msg) {
        LinkMan linkMan = GlobalData.linkManMap.get(msg.getFromID());
        if (linkMan == null) {
            DwRetrofit.getInstance().createApi(UserInfoApi.class).getUserInfo(msg.getFromID()).enqueue(new DwCallback<EntityHead<LinkMan>>() {
                @Override
                public void getBody(Call call, EntityHead<LinkMan> body) {
                    if (body.isSuccess()) {
                        body.getData().setLike(false);
                        body.getData().save();
                        GlobalData.updateLinkMans();
                        Intent broadCastIntent = new Intent(C.BROADCAST_ACTION_GET_MSG);
                        broadCastIntent.putExtra(CHAT_MSG, msg);
                        DWApplication.getInstance().sendOrderedBroadcast(broadCastIntent, null);
                    } else {
//                        TODO 获取用户信息失败
                        LogUtils.d(body.getMsg());
                    }
                }
            });
        } else {
            Intent broadCastIntent = new Intent(C.BROADCAST_ACTION_GET_MSG);
            broadCastIntent.putExtra(CHAT_MSG, msg);
            DWApplication.getInstance().sendOrderedBroadcast(broadCastIntent, null);
        }
    }

    private boolean hasAnyOurActivityOnTop(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(1);
        if (tasks != null && !tasks.isEmpty()) {
            ActivityManager.RunningTaskInfo runningTaskInfo = tasks.get(0);
            ComponentName topActivity = runningTaskInfo.topActivity;
            if (topActivity.getPackageName().equals("com.sx.dw")) {
                return true;
            }
        }

        return false;
    }
}
