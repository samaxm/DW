package com.sx.dw.im.message;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.apkfuns.logutils.LogUtils;
import com.sx.dw.core.DWApplication;
import com.sx.dw.core.GlobalData;
import com.sx.dw.core.network.DwCallback;
import com.sx.dw.core.network.DwRetrofit;
import com.sx.dw.core.network.EntityHead;
import com.sx.dw.core.network.MsgRetrofit;
import com.sx.dw.core.util.C;
import com.sx.dw.im.entity.Chat;
import com.sx.dw.im.entity.ChatMsg;
import com.sx.dw.im.entity.LikeMsg;
import com.sx.dw.im.entity.LinkMan;
import com.sx.dw.im.helper.DBUtils;
import com.sx.dw.im.helper.MsgApi;
import com.sx.dw.social.UserInfoApi;
import com.umeng.analytics.MobclickAgent;

import java.net.SocketTimeoutException;

import retrofit2.Call;
import retrofit2.Response;

import static com.sx.dw.core.GlobalData.accountInfo;
import static com.sx.dw.core.util.C.IntentExtra.CHAT_MSG;
import static com.sx.dw.im.entity.ChatMsg.TextMsgType.LIKE;

/**
 * 消息接收Service
 */
public class MsgSyncService extends Service {
    private Intent broadCastIntent;
    private UserInfoApi userInfoApi;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        runSync();
        return super.onStartCommand(intent, flags, startId);
    }



    private class SyncThread extends Thread{
        private MsgApi msgApi;
        private long maxMid;

        public SyncThread(){
            msgApi = MsgRetrofit.getInstance().createApi(MsgApi.class);
            maxMid = DBUtils.getInstance().getMaxMid();
        }
        @Override
        public void run() {
            if (TextUtils.isEmpty(accountInfo.getId())) {
                LogUtils.e("用户信息为空！");
            } else {
                while (true){
                    try {
                        Call<MessageProtos.Message> sync = msgApi.sync(maxMid, accountInfo.getId());
                        Response<MessageProtos.Message> response=sync.execute();
                        MessageProtos.Message body=response.body();
                        maxMid = body.getMid();
                        MsgMagicBox box = ProtoMsgUtils.getInstance().getMessage(body);
                        LogUtils.v(box);
                        checkMsg(box);
                    } catch (SocketTimeoutException t){
                    }catch (Exception e) {
                        try {
                            Thread.currentThread().sleep(10 * 1000);
                        } catch (InterruptedException ex) {
                        }
                    }
                }
            }
        }
    }


    private void runSync() {
        broadCastIntent = new Intent(C.BROADCAST_ACTION_GET_MSG);
        userInfoApi = DwRetrofit.getInstance().createApi(UserInfoApi.class);
        new SyncThread().start();
    }



    private void checkMsg(MsgMagicBox box){
        if(accountInfo == null){
            return;
        }
        switch (box.getType()) {
            case CHAT_TEXT: {
                initMsg(box.getChatMsg());
                break;
            }
            case LIST: {
                for (ChatMsg msg : box.getChatMsgs()) {
                    initMsg(msg);
                }
                break;
            }
            case NOTICE_LIKE: {
                initMsg(box.getLikeMsg());
                break;
            }
        }
    }

    private void initMsg(LikeMsg likeMsg) {
        ChatMsg chatMsg = new ChatMsg();
        chatMsg.setFromID(likeMsg.getLikeID());
        chatMsg.setToId(likeMsg.getBeLikedID());
        chatMsg.setTextMsgType(LIKE);
        chatMsg.setTime(likeMsg.getTime());
        chatMsg.setText(likeMsg.getName() + "收藏了你");
        initChatMsg(chatMsg);
    }

    private void initMsg(ChatMsg msg) {
        msg.setTextMsgType(ChatMsg.TextMsgType.GET);
        initChatMsg(msg);
    }



    /**
     * 初始化消息，判断本地是否有会话，无则创建并储存；判断本地是否有联系人，无则发起同步网络请求
     * @param msg 经过处理最终存储的消息
     */
    private void initChatMsg(ChatMsg msg){
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
            userInfoApi.getUserInfo(msg.getFromID()).enqueue(new DwCallback<EntityHead<LinkMan>>() {
                @Override
                public void getBody(Call call, EntityHead<LinkMan> body) {
                    if (body.isSuccess()) {
                        body.getData().save();
                        GlobalData.updateLinkMans();
                        broadCastIntent.putExtra(CHAT_MSG, msg);
                        sendOrderedBroadcast(broadCastIntent, null);
                    } else {
//                        TODO 获取用户信息失败
                        LogUtils.d(body.getMsg());
                    }
                }
            });
        } else {
            broadCastIntent.putExtra(CHAT_MSG, msg);
            sendOrderedBroadcast(broadCastIntent, null);
        }
    }


//    private void syncMsg() {
//        LogUtils.d("syncMsg()");
//        if (accountInfo == null) {
//            return;
//        }
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (true) {
//                    Call<MessageProtos.Message> sync = msgApi.sync(maxMid, accountInfo.getId());
//                    try {
//                        sync.execute();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }).start();
//        Call<MessageProtos.Message> sync = msgApi.sync(maxMid, accountInfo.getId());
//        sync.enqueue(new DwCallback<MessageProtos.Message>() {
//            @Override
//            public void getBody(Call call, final MessageProtos.Message body) {
//                new Thread() {
//                    @Override
//                    public void run() {
//                        maxMid = body.getMid();
//                        MsgMagicBox box = ProtoMsgUtils.getInstance().getMessage(body);
//                        LogUtils.v(box);
//                        checkMsg(box);
//                        syncMsg();
//                    }
//                }.start();
//            }
//
//            @Override
//            public void onFailure(Call<MessageProtos.Message> call, Throwable t) {
//                if (t instanceof SocketTimeoutException) {
//                    syncMsg();
//                } else if (t instanceof NullBodyException) {
//                    new Thread() {
//                        @Override
//                        public void run() {
//                            LogUtils.e("数据为空");
//                            try {
//                                Thread.currentThread().sleep(10 * 1000);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            } finally {
//                                syncMsg();
//                            }
//                        }
//                    }.start();
//                } else {
//                    super.onFailure(call, t);
//                }
//
//            }
//        });
//
//    }

}
