package com.sx.dw.social.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.sx.dw.R;
import com.sx.dw.im.entity.Chat;
import com.umeng.analytics.MobclickAgent;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @Description: 会话列表适配器
 * @author: fanjie
 * @date: 2016/10/11 11:11
 */

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatViewHolder>{

    private Context context;
    private List<Chat> chats;
    private OnItemClickListener listener;

    public ChatListAdapter(Context context, OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setData(List<Chat> chats){
        this.chats = chats;
        Collections.sort(chats, new Comparator<Chat>() {
            @Override
            public int compare(Chat o1, Chat o2) {
                return -(int)(o1.getTime()-o2.getTime());
            }
        });
        notifyDataSetChanged();
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ChatViewHolder(LayoutInflater.from(context).inflate(R.layout.listitem_chat_list,parent,false));
    }

    @Override
    public void onBindViewHolder(ChatViewHolder h, int position) {
        final Chat chat = chats.get(position);
        if(chat.getLinkMan() == null){
            MobclickAgent.reportError(context,"会话联系人为空");
            return;
        }
        h.setChat(chat);
        h.sdvUserIcon.setImageURI(chat.getLinkMan().getPrimaryIcon());
        h.tvUserName.setText(chat.getLinkMan().getName());
        h.tvTime.setText(chat.getTimeToShow());
        String count = chat.getUnreadMsgCount();
        if(!TextUtils.isEmpty(count)) {
            h.tvUnreadMsg.setVisibility(View.VISIBLE);
            h.tvUnreadMsg.setText(count);
        }else {
            h.tvUnreadMsg.setVisibility(View.INVISIBLE);
        }
        if(chat.getLastMsg() != null) {
            h.tvLastMsg.setText(chat.getLastMsg().getTicker());
        }
        h.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(chat);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (chats == null) {
            return 0;
        }else {
            return chats.size();
        }
    }

    class ChatViewHolder extends RecyclerView.ViewHolder{
        View root;
        SimpleDraweeView sdvUserIcon;
        TextView tvUserName;
        TextView tvLastMsg;
        TextView tvTime;
        TextView tvUnreadMsg;
        private Chat chat;

        public Chat getChat() {
            return chat;
        }

        public void setChat(Chat chat) {
            this.chat = chat;
        }

        public ChatViewHolder(View v) {
            super(v);
            root = v;
            sdvUserIcon = (SimpleDraweeView) v.findViewById(R.id.sdv_user_icon);
            tvUserName = (TextView) v.findViewById(R.id.tv_user_name);
            tvLastMsg = (TextView) v.findViewById(R.id.tv_last_msg);
            tvTime = (TextView) v.findViewById(R.id.tv_time);
            tvUnreadMsg = (TextView) v.findViewById(R.id.tv_unread_msg);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(Chat chat);
    }
}
