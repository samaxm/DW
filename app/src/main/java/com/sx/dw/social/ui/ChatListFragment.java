package com.sx.dw.social.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.activeandroid.query.Select;
import com.apkfuns.logutils.LogUtils;
import com.sx.dw.R;
import com.sx.dw.im.ui.ChatActivity;
import com.sx.dw.im.entity.Chat;

import java.util.List;

import static com.sx.dw.core.util.C.RequestCode.CHAT_LIST_TO_CHAT;

/**
 * 会话列表片段
 */
public class ChatListFragment extends Fragment {

    private RecyclerView rvChatList;
    private ChatListAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_chat_list, container, false);
        rvChatList = (RecyclerView) root.findViewById(R.id.rv_chat_list);
        adapter = new ChatListAdapter(getContext(),onItemClickListener);
        rvChatList.setLayoutManager(new LinearLayoutManager(getContext()));
//        rvChatList.addItemDecoration(new DividerItemDecoration(
//                getActivity(), DividerItemDecoration.HORIZONTAL_LIST));
        rvChatList.setAdapter(adapter);
        List<Chat> chats = new Select().from(Chat.class).execute();
        adapter.setData(chats);
        return root;
    }

    public void refreshData(){
        LogUtils.d("refreshData");
        List<Chat> chats = new Select().from(Chat.class).execute();
        adapter.setData(chats);
    }

    private ChatListAdapter.OnItemClickListener onItemClickListener = new ChatListAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(Chat chat) {
            ChatActivity.startMe(getActivity(),chat.getLinkMan(), CHAT_LIST_TO_CHAT);
        }
    };

}
