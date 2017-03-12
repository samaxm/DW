package com.sx.dw.im.ui;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.apkfuns.logutils.LogUtils;
import com.sx.dw.R;
import com.sx.dw.core.BaseActivity;
import com.sx.dw.core.network.DwCallback;
import com.sx.dw.core.network.MsgRetrofit;
import com.sx.dw.core.util.C;
import com.sx.dw.core.util.ToastUtil;
import com.sx.dw.im.entity.Chat;
import com.sx.dw.im.entity.ChatMsg;
import com.sx.dw.im.entity.LinkMan;
import com.sx.dw.im.entity.WealthAckMsg;
import com.sx.dw.im.helper.MsgApi;
import com.sx.dw.im.message.MessageProtos;
import com.sx.dw.im.message.MsgMagicBox;
import com.sx.dw.im.message.MsgSyncListener;
import com.sx.dw.im.message.MsgSyncReceiverInChat;
import com.sx.dw.im.message.ProtoMsgUtils;
import com.sx.dw.social.ui.UserInfoActivity;
import com.sx.dw.wealth.WealthHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;

import retrofit2.Call;

import static com.sx.dw.core.GlobalData.accountInfo;
import static com.sx.dw.core.GlobalData.chatMap;
import static com.sx.dw.core.GlobalData.linkManMap;
import static com.sx.dw.core.GlobalData.setAccountInfo;
import static com.sx.dw.core.GlobalData.updateChats;
import static com.sx.dw.core.GlobalData.updateLinkMans;
import static com.sx.dw.core.util.C.IntentExtra.LINKMAN;
import static com.sx.dw.core.util.C.IntentExtra.REQUEST_CODE;
import static com.sx.dw.core.util.C.RequestCode.MSG_LIST_TO_USER_INFO;

/**
 * 及时通讯页面
 */
public class ChatActivity extends BaseActivity {

    private TextView tvTitle;
    private TextView tvLinkManWorth;
    private LinearLayout llAlertLayout;
    private TextView tvAlert;
    private ImageView ivShutAlert;
    private SwipeRefreshLayout srlMsgList;
    private RecyclerView rvMsgList;
    private TextView tvMyWealth;
    private EditText etInputMsg;
    private TextView tvSend;

    private LinkMan linkMan;
    private Chat chat;
    private MsgListAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private MsgSyncReceiverInChat msgSyncReceiver;
    private IntentFilter msgFilter;
    private MsgHelper msgHelper;
    private MsgApi msgApi;
    private Set<Call<MessageProtos.Message>> sendingCalls;

    private int requestType;
    private boolean isFirstChat;
    private int msgIndex;
    private int continueMsgIndex;


    private MsgSyncListener msgSyncListener = new MsgSyncListener() {
        @Override
        public void onMsgGet(ChatMsg msg) {
            if (msg.getFromID().equals(linkMan.getDwID())) {
                //收到消息，属于本会话，则截拦消息
                msgSyncReceiver.abortBroadcast();
                msg.setUnreadMsg(false);
                continueMsgIndex = 0;
                adapter.addMsg(msg);
                rvMsgList.scrollToPosition(adapter.getItemCount() - 1);
                tvMyWealth.setText(accountInfo.getWealthString());
            }
        }
    };

    public static void startMe(Activity activity, @Nonnull LinkMan entity, int requestCode) {
        Intent intent = new Intent(activity, ChatActivity.class);
        intent.putExtra(LINKMAN, entity);
        intent.putExtra(REQUEST_CODE, requestCode);
        activity.startActivityForResult(intent, requestCode);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvLinkManWorth = (TextView) findViewById(R.id.tv_worth);
        llAlertLayout = (LinearLayout) findViewById(R.id.ll_alert_layout);
        tvAlert = (TextView) findViewById(R.id.tv_alert);
        ivShutAlert = (ImageView) findViewById(R.id.iv_shut_alert);
        srlMsgList = (SwipeRefreshLayout) findViewById(R.id.srl_msg_list);
        rvMsgList = (RecyclerView) findViewById(R.id.rv_msg_list);
        tvMyWealth = (TextView) findViewById(R.id.tv_my_wealth);
        etInputMsg = (EditText) findViewById(R.id.et_input_msg);
        tvSend = (TextView) findViewById(R.id.tv_send);

        initValue();
        initView();

    }


    private void initValue() {
        linkMan = (LinkMan) getIntent().getSerializableExtra(LINKMAN);
        requestType = getIntent().getIntExtra(REQUEST_CODE, 0);

        LinkMan model = linkManMap.get(linkMan.getDwID());

        if (model != null) {
            linkMan = model.update(linkMan);
        } else {
            linkMan.save();
            updateLinkMans();
        }

        chat = chatMap.get(linkMan.getDwID());
        if (chat == null) {
            isFirstChat = true;
            chat = new Chat();
            chat.setLinkManId(linkMan.getDwID());
        }
        chat.cleanUnread();
//        在其他方式进入时，如果通知栏有消息则清除
        if (chat.getId() != null) {
//            通知可以配置点击自动消失
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(Integer.parseInt(chat.getId() + ""));
        }


        adapter = new MsgListAdapter(this, linkMan, onItemClickListener);
        if (chat.getChatMsgs() == null) {
            chat.setChatMsgs(new ArrayList<ChatMsg>());
            chat.setChatMsgs(chat.getChatMsgs());
        }
        msgIndex++;
        adapter.setData(chat.getChatMsgs(msgIndex));
        msgHelper = new MsgHelper(this);
        msgApi = MsgRetrofit.getInstance().createApi(MsgApi.class);
        sendingCalls = new HashSet<>();
    }

    private void initView() {
        tvTitle.setText(linkMan.getName());
        tvLinkManWorth.setText(linkMan.getWorthString());
        if (isFirstChat) {
            llAlertLayout.setVisibility(View.VISIBLE);
            ivShutAlert.setOnClickListener(onClickListener);
        }
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        srlMsgList.setOnRefreshListener(onRefreshListener);
        rvMsgList.setLayoutManager(linearLayoutManager);
        rvMsgList.setItemAnimator(new DefaultItemAnimator());
        rvMsgList.setAdapter(adapter);
        tvMyWealth.setText(accountInfo.getWealthString());
        tvSend.setOnClickListener(onClickListener);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (msgSyncReceiver == null || msgFilter == null) {
            msgFilter = new IntentFilter();
            msgFilter.addAction(C.BROADCAST_ACTION_GET_MSG);
            msgFilter.setPriority(C.BROADCAST_PRIORITY_IN_CHAT);
            msgSyncReceiver = new MsgSyncReceiverInChat(msgSyncListener);
        }
        registerReceiver(msgSyncReceiver, msgFilter);
        WealthHelper.getInstance().getUserWealth(tvMyWealth);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(msgSyncReceiver);
        saveData();
    }

    private void saveData() {
        setAccountInfo(accountInfo);
        linkMan.save();
        if (adapter.msgNotEmpty()) {
//            遍历还没有得到结果的发送请求，依次取消掉
            for (Call<MessageProtos.Message> call : sendingCalls) {
//                cancel掉call后会返回:onFailure(): java.io.IOException: Canceled
                LogUtils.d(call);
                call.cancel();
            }
            adapter.saveData();
            if (chatMap.get(chat.getLinkManId()) == null) {
                chat.save();
                updateChats();
            }
        }
    }


    public void send(final ChatMsg chatMsg, boolean isNewMsg) {
        if (accountInfo.getWealth() < 1) {
            ToastUtil.showToast("身家不足哦亲");
            if (!isNewMsg) {
                adapter.msgFailure(chatMsg.getTempID());
            }
            return;
        }
        if (isNewMsg) {
            if(++continueMsgIndex>3){
                chatMsg.setStatus(MessageProtos.ChargeStatus.ORVERFLOW);
            }else{
                chatMsg.setStatus(MessageProtos.ChargeStatus.FREE);
            }
            adapter.addMsg(chatMsg);
            rvMsgList.scrollToPosition(adapter.getItemCount() - 1);
            etInputMsg.setText("");
        }
        final Call<MessageProtos.Message> sendingCall = msgApi.send(ProtoMsgUtils.getInstance().toBody(chatMsg));
        sendingCalls.add(sendingCall);

        sendingCall.enqueue(new DwCallback<MessageProtos.Message>() {
            @Override
            public void getBody(Call call, MessageProtos.Message body) {
                MsgMagicBox box = ProtoMsgUtils.getInstance().getMessage(body);
                LogUtils.v(box);
                if (box.getType() == MessageProtos.Message.MessageType.COMMAND_WEALTH_ACK) {
//                  应该封装一下这些乱七八糟的
                    WealthAckMsg wealthAckMsg = box.getWealthAckMsg();
                    if (wealthAckMsg.isChargeSuccess()) {
                        int wealth = wealthAckMsg.getWealth();
                        accountInfo.setWealth(wealth);
                        tvMyWealth.setText(accountInfo.getWealthString());
                        adapter.msgSuccessed(wealthAckMsg.getTempID(), wealthAckMsg.getMid());

                    } else {
                        ToastUtil.showToast("身家不足哦");
                        adapter.msgFailure(chatMsg.getTempID());
                    }
                }
//              当请求有结果后，去掉Call
                sendingCalls.remove(call);
            }

            @Override
            public void onFailure(Call<MessageProtos.Message> call, Throwable t) {
                super.onFailure(call, t);
//               当请求有结果后，去掉Call
                adapter.msgFailure(chatMsg.getTempID());
                sendingCalls.remove(call);
            }
        });
    }

    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            if (!chat.isGetAllData()) {
                msgIndex++;
                adapter.setData(chat.getChatMsgs(msgIndex));
            }
            srlMsgList.setRefreshing(false);
        }
    };



    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.iv_shut_alert: {
                    llAlertLayout.setVisibility(View.GONE);
                    break;
                }
                case R.id.tv_send: {
                    String msgStr = etInputMsg.getText().toString();
                    if (!TextUtils.isEmpty(msgStr)) {
                        ChatMsg chatMsg = new ChatMsg();
                        chatMsg.setFromID(accountInfo.getId());
                        chatMsg.setToId(linkMan.getDwID());
                        chatMsg.setText(msgStr);
                        chatMsg.setTextMsgType(ChatMsg.TextMsgType.SEND);
//                        chatMsg.setMessageType(MessageProtos.ChatMessageType.TEXT);
                        chatMsg.setChat(chat);
                        chatMsg.setMid(-System.currentTimeMillis());
                        chatMsg.setTempID(System.currentTimeMillis() + "");
                        chatMsg.setTime(System.currentTimeMillis());
                        send(chatMsg, true);
                    }
                }
            }
        }
    };

    private MsgListAdapter.OnItemClickListener onItemClickListener = new MsgListAdapter.OnItemClickListener() {
        @Override
        public void onIconClick(LinkMan entity) {
            UserInfoActivity.startMe(ChatActivity.this, entity, MSG_LIST_TO_USER_INFO);
        }

        @Override
        public void onFailureIconClick(ChatMsg chatMsg) {
            send(chatMsg, false);
        }
    };


}
