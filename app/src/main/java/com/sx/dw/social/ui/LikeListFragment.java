package com.sx.dw.social.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apkfuns.logutils.LogUtils;
import com.sx.dw.R;
import com.sx.dw.core.GlobalData;
import com.sx.dw.im.entity.LinkMan;
import com.sx.dw.im.ui.ChatActivity;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.sx.dw.core.GlobalData.linkManMap;
import static com.sx.dw.core.util.C.RequestCode.LIKE_LIST_TO_CHAT;
import static com.sx.dw.core.util.C.RequestCode.LIKE_LIST_TO_USER_INFO;

/**
 * 收藏用户列表片段
 */

public class LikeListFragment extends Fragment {

    private RecyclerView rvLikeList;
    private LikeListAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_like_list, container, false);
        rvLikeList = (RecyclerView) root.findViewById(R.id.rv_like_list);
        adapter = new LikeListAdapter(getContext(), onItemClickListener);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true);

        rvLikeList.setLayoutManager(layoutManager);
        rvLikeList.setAdapter(adapter);
        rvLikeList.setNestedScrollingEnabled(false);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        pullHttpDate();
    }

    private void pullHttpDate() {
        //直接从本地取数据
        List<LinkMan> lists=new LinkedList<>();
        if(linkManMap!=null){
            for (Map.Entry<String,LinkMan> set: GlobalData.linkManMap.entrySet()){
                if(set.getValue().isLike())
                    lists.add(set.getValue());
            }
        }
        Collections.sort(lists, new Comparator<LinkMan>() {
                    @Override
                    public int compare(LinkMan o1, LinkMan o2) {
                       if(o1.getLikeDate()-o2.getLikeDate()>0){
                           return 1;
                       }else if(o1.getLikeDate()-o2.getLikeDate()<0){
                           return -1;
                       }else {
                           return 0;
                       }
                    }
                });
        adapter.setData(lists);
////        // FIXME: 2016/10/25  三星 GALAXY C5(SM-C5000)  java.lang.IllegalArgumentException: Unable to create converter for com.sx.dw.communal.network.EntityHead>
////        // FIXME: 2016/10/25 三星 三星2016版GALAXY A9(SM-A9000) java.lang.IllegalArgumentException: Unable to create converter for com.sx.dw.communal.network.EntityHead>
////        // FIXME: 2016/10/25 三星 SM-N9109W java.lang.IllegalArgumentException: Unable to create converter for com.sx.dw.communal.network.EntityHead>
//        api.getLikeList(accountInfo.getId()).enqueue(new DwCallback<EntityHead<List<LikeRecordEntity>>>() {
//            @Override
//            public void getBody(Call call, EntityHead<List<LikeRecordEntity>> body) {
//                if (body.isSuccess()) {
//                    likes = body.getData();
//                    adapter.setData(likes);
//                    for (LikeRecordEntity entity: likes) {
//                        LinkMan linkMan = entity.getInfo();
//                        if(linkMan == null){
//                            break;
//                        }
//                        LogUtils.v(linkMan);
//                        // FIXME: 2016/11/16 这里空指针异常 java.lang.String com.sx.dw.im.entity.LinkMan.getDwID()' on a null object reference
//                        linkMan.setLike(true);
//                        LinkMan model = linkManMap.get(linkMan.getDwID());
//                        if (model != null) {
//                            model.update(linkMan);
//                        } else {
//                            linkMan.save();
//                            GlobalData.updateLinkMans();
//                        }
//                    }
//                } else {
//                    ToastUtil.showToast(body.getMsg());
//                }
//            }
//        });
    }

    private LikeListAdapter.OnItemClickListener onItemClickListener = new LikeListAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(LinkMan entity) {
            LogUtils.d(entity);
            UserInfoActivity.startMe(getActivity(), entity, LIKE_LIST_TO_USER_INFO);
        }

        @Override
        public void onBtnClick(LinkMan entity) {
            ChatActivity.startMe(getActivity(), entity, LIKE_LIST_TO_CHAT);
        }
    };

    public void refreshData() {
        LogUtils.d("refreshData");
        pullHttpDate();
    }
}
