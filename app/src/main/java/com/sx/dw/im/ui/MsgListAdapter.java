package com.sx.dw.im.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.apkfuns.logutils.LogUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.sx.dw.R;
import com.sx.dw.core.GlobalData;
import com.sx.dw.core.util.C;
import com.sx.dw.im.entity.ChatMsg;
import com.sx.dw.im.entity.LinkMan;
import com.sx.dw.im.message.MessageProtos;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static com.sx.dw.core.GlobalData.accountInfo;

/**
 * 消息列表适配器
 */

/**
 * FIXME:服务端和客户端的状态不统一，服务端为0 free ,1 normal, 2 overflow ，客户端为0 normal, 1 free, 2 overflow
 */
public class MsgListAdapter extends RecyclerView.Adapter<MsgListAdapter.MsgViewHolder> {


    private Context context;
    private OnItemClickListener onItemClickListener;
    private List<ChatMsg> chatMsgs;
    private LinkMan linkMan;
    private HashMap<String, ChatMsg> changeableMsg;
    private Set<Long> idFilter;

    public MsgListAdapter(Context context, LinkMan linkMan, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.linkMan = linkMan;
        this.onItemClickListener = onItemClickListener;
        changeableMsg = new HashMap<>();
        idFilter=new HashSet<>();
        this.chatMsgs=new LinkedList<>();
    }

    public void setData(List<ChatMsg> chatMsgs) {
        this.chatMsgs.clear();
        idFilter.clear();
        LogUtils.d(chatMsgs);
//        this.chatMsgs = chatMsgs;
        Collections.sort(chatMsgs);
//        FIXME 更换吊炸天的策略 DiffUtil
        long showTime = 0;
        int index=0;
        for (int i = 0; i < chatMsgs.size(); i++) {
            ChatMsg c = chatMsgs.get(i);
            if(isRepeated(c.getMid())){
                continue;
            }
            if (c.getMsgState() != ChatMsg.MsgState.SUCCESSED) {
                changeableMsg.put(c.getTempID(), c);
            }
            if(c.getStatus() == MessageProtos.ChargeStatus.ORVERFLOW&&i>0&&chatMsgs.get(i-1).getStatus()== MessageProtos.ChargeStatus.FREE){
                ChatMsg msg=new ChatMsg();
                msg.setStatus(MessageProtos.ChargeStatus.NOMAL);
                msg.setTextMsgType(ChatMsg.TextMsgType.NOTICE);
                msg.setTime(chatMsgs.get(i-1).getTime());
                if(c.getTextMsgType()== ChatMsg.TextMsgType.GET) {
                    msg.setText("您已经超过3条消息未回复对方，接下来您将无法获取到对方消息的收益");
                }else{
                    msg.setText("对方已经超过3条消息未回复，接下来对方将无法获取到您消息的收益");
                }
                index++;
                this.chatMsgs.add(msg);
            }
            index++;
            this.chatMsgs.add(c);
            c.setListPosition(index);
            //        时间差超过指定分钟就显示时间
            if (Math.abs(c.getTime() - showTime) > 60 * 1000) {
                showTime = c.getTime();
                c.setShowTime(true);
            }
        }
        notifyDataSetChanged();
    }

    public void saveData() {
        for (ChatMsg msg : changeableMsg.values()) {
            if (msg.getMsgState() == ChatMsg.MsgState.SENDING) {
                msg.setMsgState(ChatMsg.MsgState.FAILURE);
                msg.save();
            }
        }
    }

    private boolean isRepeated(Long mid){
        if(idFilter.contains(mid)){
            return true;
        }else{
            idFilter.add(mid);
            return false;
        }
    }

    public void addMsg(ChatMsg msg) {
        LogUtils.v(msg);
        if (chatMsgs == null) {
            throw new NullPointerException("需要先调用setData");
        }
        if(isRepeated(msg.getMid())){
            return;
        }
        if(msg.getStatus() == MessageProtos.ChargeStatus.ORVERFLOW&&getItemCount()!=1&&
                chatMsgs.get(getItemCount()-1).getStatus()== MessageProtos.ChargeStatus.FREE){
            ChatMsg notice=new ChatMsg();
            notice.setStatus(MessageProtos.ChargeStatus.NOMAL);
            notice.setTextMsgType(ChatMsg.TextMsgType.NOTICE);
            if(msg.getTextMsgType()== ChatMsg.TextMsgType.GET)
                notice.setText("您已经超过3条消息未回复对方，接下来您将无法获取到对方消息的收益");
            else
                notice.setText("对方已经超过3条消息未回复，接下来对方将无法获取到您消息的收益");

            chatMsgs.add(notice);
            notifyItemInserted(getItemCount() - 1);
        }

//        if(msg.getTextMsgType() == ChatMsg.TextMsgType.GET&&msg.getStatus() == MessageProtos.ChargeStatus.ORVERFLOW){
//            ToastUtil.showToast("您已经超过3条消息未回复对方，接下来您将无法获取到对方消息的收益");
//            msg.setStatus(null);
//            msg.save();
//        }
        chatMsgs.add(msg);
//        Collections.sort(chatMsgs);
        changeableMsg.put(msg.getTempID(), msg);
        msg.setListPosition(getItemCount() - 1);
        notifyItemInserted(getItemCount() - 1);
        msg.save();
    }

    public void msgSuccessed(String tempID,long mid) {
        ChatMsg msg = changeableMsg.get(tempID);
        if (msg != null && msg.getMsgState() != ChatMsg.MsgState.SUCCESSED) {
            msg.setMsgState(ChatMsg.MsgState.SUCCESSED);
            msg.setMid(mid);
            notifyDataSetChanged();
            msg.save();
        }
    }

    public void msgFailure(String tempID) {
        ChatMsg msg = changeableMsg.get(tempID);
        if (msg != null) {
            msg.setMsgState(ChatMsg.MsgState.FAILURE);
            notifyDataSetChanged();
            msg.save();
        }
    }

    @Override
    public MsgViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MsgViewHolder(LayoutInflater.from(context).inflate(R.layout.listitem_msg, parent, false));
    }

    @Override
    public void onBindViewHolder(final MsgViewHolder h, int position) {
        final ChatMsg msg = chatMsgs.get(position);
        if (msg.isShowTime()) {
            h.tvTime.setVisibility(View.VISIBLE);
            h.tvTime.setText(msg.getTimeToShow());
        } else {
            h.tvTime.setVisibility(View.GONE);
        }

        switch (msg.getTextMsgType()) {
            case NOTICE:{
                h.tvTime.setText(msg.getText());
                h.tvTime.setVisibility(View.VISIBLE);
                h.sendRoot.setVisibility(View.GONE);
                h.likeRoot.setVisibility(View.GONE);
                h.getRoot.setVisibility(View.GONE);
                break;
            }
            case LIKE: {
                h.likeRoot.setVisibility(View.VISIBLE);
                h.sendRoot.setVisibility(View.GONE);
                h.getRoot.setVisibility(View.GONE);
                h.sdvLikeIcon.setImageURI(linkMan.getPrimaryIcon());
                h.sdvLikeIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickListener.onIconClick(linkMan);
                    }
                });
                LinkMan man= GlobalData.linkManMap.get(msg.getFromID());
                if(man!=null&& C.STAR.equals(man.getType()))
                    h.tvLikeMsg.setText(msg.getText()+"\n"+"来自"+man.getTag()+"的VIP客户");
                else
                h.tvLikeMsg.setText(msg.getText());
                break;
            }
            case GET: {
                h.getRoot.setVisibility(View.VISIBLE);
                h.likeRoot.setVisibility(View.GONE);
                h.sendRoot.setVisibility(View.GONE);

                h.sdvUserIcon.setImageURI(linkMan.getPrimaryIcon());
                h.sdvUserIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onItemClickListener.onIconClick(linkMan);
                    }
                });
                // FIXME: 2016/11/28 
                h.tvGetMsg.setText(msg.getText());
                break;
            }
            case SEND: {
                h.sendRoot.setVisibility(View.VISIBLE);
                h.likeRoot.setVisibility(View.GONE);
                h.getRoot.setVisibility(View.GONE);
                h.sdvMyIcon.setImageURI(accountInfo.getPrimaryIcon());
                // FIXME: 2016/11/28
                h.tvSendMsg.setText(msg.getText());
                h.sendRoot.setVisibility(View.VISIBLE);
                h.sdvMyIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onItemClickListener.onIconClick(accountInfo.toLinkMan());
                    }
                });
                h.ivIcMsgFailure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        h.proBarSendMsg.setVisibility(View.VISIBLE);
                        h.ivIcMsgFailure.setVisibility(View.GONE);
                        msg.setListPosition(h.getAdapterPosition());
                        onItemClickListener.onFailureIconClick(msg);
                    }
                });
                switch (msg.getMsgState()) {
                    case FAILURE: {
                        h.proBarSendMsg.setVisibility(View.GONE);
                        h.ivIcMsgFailure.setVisibility(View.VISIBLE);
                        break;
                    }
                    case SUCCESSED: {
                        h.proBarSendMsg.setVisibility(View.GONE);
                        h.ivIcMsgFailure.setVisibility(View.GONE);
                        break;
                    }
                    case SENDING: {
                        h.proBarSendMsg.setVisibility(View.VISIBLE);
                        h.ivIcMsgFailure.setVisibility(View.GONE);
                        break;
                    }

                }
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        if (chatMsgs == null) {
            return 0;
        } else {
            return chatMsgs.size();
        }
    }

    public boolean msgNotEmpty() {
        return chatMsgs!=null && chatMsgs.size()>0;
    }


    class MsgViewHolder extends RecyclerView.ViewHolder {
        View root;
        View likeRoot;
        View getRoot;
        View sendRoot;
        TextView tvTime;
        SimpleDraweeView sdvLikeIcon;
        SimpleDraweeView sdvUserIcon;
        SimpleDraweeView sdvMyIcon;
        TextView tvLikeMsg;
        TextView tvGetMsg;
        TextView tvSendMsg;
        ProgressBar proBarSendMsg;
        ImageView ivIcMsgFailure;

        MsgViewHolder(View v) {

            super(v);

            root = v;
            likeRoot = v.findViewById(R.id.ll_like_layout);
            getRoot = v.findViewById(R.id.ll_get_msg_layout);
            sendRoot = v.findViewById(R.id.ll_send_msg_layout);
            tvTime = (TextView) v.findViewById(R.id.tv_time);
            sdvLikeIcon = (SimpleDraweeView) v.findViewById(R.id.sdv_like_icon);
            sdvUserIcon = (SimpleDraweeView) v.findViewById(R.id.sdv_user_icon);
            sdvMyIcon = (SimpleDraweeView) v.findViewById(R.id.sdv_my_icon);
            tvLikeMsg = (TextView) v.findViewById(R.id.tv_like_msg);
            tvGetMsg = (TextView) v.findViewById(R.id.tv_get_msg);
            tvSendMsg = (TextView) v.findViewById(R.id.tv_send_msg);


            proBarSendMsg = (ProgressBar) v.findViewById(R.id.proBar_send_msg);
            ivIcMsgFailure = (ImageView) v.findViewById(R.id.iv_ic_msg_failure);
        }
    }

    public interface OnItemClickListener {
        void onIconClick(LinkMan entity);

        void onFailureIconClick(ChatMsg chatMsg);
    }
}
