package com.sx.dw.social.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.sx.dw.R;
import com.sx.dw.core.BaseActivity;
import com.sx.dw.core.network.DwRetrofit;
import com.sx.dw.core.network.EntityHead;
import com.sx.dw.im.entity.LinkMan;
import com.sx.dw.im.ui.ChatActivity;
import com.sx.dw.social.UserInfoApi;
import com.sx.dw.social.entity.SearchUserBean;

import java.util.LinkedList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.sx.dw.core.util.C.RequestCode.SEARCH_TO_CHAT;
import static com.sx.dw.core.util.C.RequestCode.SEARCH_TO_INFO;

/**
 * @Description: TODO 添加类的描述
 * @author: fanjie
 * @date: 2017/1/7 17:02
 */

public class SearchActivity extends BaseActivity implements View.OnClickListener{


    private ImageView back;
    private ImageView clear;
    private TextView search;
    private TextView search_text;
    private RecyclerView list;
    private SearchListAdapter adapter;
    private UserInfoApi api;
    private String latsSearch;
    private TextView empty_hint;
    private int page=0;


    public static void startMe(Context context){
        Intent intent=new Intent(context, SearchActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        initView();
        initHelper();
    }

    private void initView(){
        setContentView(R.layout.search_layout);
        back= (ImageView) findViewById(R.id.back_btn);
        empty_hint= (TextView) findViewById(R.id.empty_hint);
        clear= (ImageView) findViewById(R.id.clear_text);
        search= (TextView) findViewById(R.id.search_button);
        search_text= (TextView) findViewById(R.id.search_text);
        list= (RecyclerView) findViewById(R.id.search_list);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL_LIST));
        adapter=new SearchListAdapter();
        list.setAdapter(adapter);
        back.setOnClickListener(this);
        clear.setOnClickListener(this);
        search.setOnClickListener(this);
    }

    public void initHelper(){
        api= DwRetrofit.getInstance().createApi(UserInfoApi.class);
    }


    @Override
    public void onClick(View v) {
            switch (v.getId()){
                case R.id.back_btn:
                    UserCenterActivity.startMe(this);
                    finish();
                    break;
                case R.id.clear_text:
                    search_text.setText("");
                    break;
                case R.id.search_button:
                    String search_condition=search_text.getText().toString();
                    if(search_condition!=null&&!search_condition.equals("")&&!search_condition.equals(latsSearch)){
                        latsSearch=search_condition;
                        page=0;
                        api.searchUser(latsSearch,page).enqueue(new Callback<EntityHead<List<SearchUserBean>>>() {
                            @Override
                            public void onResponse(Call<EntityHead<List<SearchUserBean>>> call, Response<EntityHead<List<SearchUserBean>>> response) {
                                adapter.clearData();
                                if(response.body()!=null&&response.body().getData().size()!=0){
                                    empty_hint.setVisibility(View.GONE);
                                    List<SearchUserBean> results=response.body().getData();
                                    adapter.setList(results);
                                }else {
                                    empty_hint.setVisibility(View.VISIBLE);
                                }
                            }
                            @Override
                            public void onFailure(Call<EntityHead<List<SearchUserBean>>> call, Throwable t) {
                                adapter.clearData();
                                empty_hint.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                    break;
            }

    }

    class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.SearchItemViewHolder>{

        private List<SearchUserBean> list;
        @Override
        public SearchItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            SearchItemViewHolder holder = new SearchItemViewHolder(LayoutInflater.from(
                    SearchActivity.this).inflate(R.layout.search_list_item, parent,
                    false));
            return holder;
        }

        public void addUser(SearchUserBean user){
            if(list==null){
                list=new LinkedList<>();
            }
            if(checkUser(user)) {
                list.add(user);
                notifyItemInserted(list.size() - 1);
            }
        }

        private boolean checkUser(SearchUserBean user){
            return true;
        }

        @Override
        public void onBindViewHolder(SearchItemViewHolder holder, int position) {
            SearchUserBean user=list.get(position);
            holder.setBean(user);
            holder.tag.setText(user.getSign());
            holder.name.setText(user.getName());
            holder.icon.setImageURI(user.getPrimaryIcon());
            holder.icon.setOnClickListener(new ShowUserInfoLisener(user));

            if(user.getSex().equals("男")){
                holder.sex.setImageResource(R.drawable.icon_sex_man_x3);
            }else{
                holder.sex.setImageResource(R.drawable.icon_sex_wuman_x3);
            }
            holder.msg_btn.setVisibility(View.VISIBLE);
            holder.msg_btn.setOnClickListener(new SendMessageLisener(position));
        }

        @Override
        public int getItemCount() {
            return  list==null? 0: list.size();
        }

        public void clearData(){
            if(list!=null){
                list.clear();
                notifyDataSetChanged();
            }
        }

        public void setList(List<SearchUserBean> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        class SearchItemViewHolder extends RecyclerView.ViewHolder {

            private TextView name;
            private TextView tag;
            private SimpleDraweeView icon;
            private ImageView sex;
            private TextView msg_btn;
            private SearchUserBean bean;
            public SearchUserBean getBean() {
                return bean;
            }
            public void setBean(SearchUserBean bean) {
                this.bean = bean;
            }

            public SearchItemViewHolder(View itemView) {
                super(itemView);
                name= (TextView) itemView.findViewById(R.id.user_name);
                tag= (TextView) itemView.findViewById(R.id.user_tag);
                icon= (SimpleDraweeView) itemView.findViewById(R.id.iv_user_icon);
                sex= (ImageView) itemView.findViewById(R.id.sex_icon);
                msg_btn= (TextView) itemView.findViewById(R.id.msg_btn);
            }
        }

        class ShowUserInfoLisener implements View.OnClickListener{
            private SearchUserBean user;


            public ShowUserInfoLisener(SearchUserBean bean){
                this.user=bean;
            }

            @Override
            public void onClick(View v) {
                LinkMan linkMan=new LinkMan(user);
                UserInfoActivity.startMe(SearchActivity.this,linkMan,SEARCH_TO_INFO);
            }
        }



        class SendMessageLisener implements View.OnClickListener {

            private int index;


            public SendMessageLisener(int index){
                this.index=index;
            }


            @Override
            public void onClick(View v) {
                SearchUserBean bean=list.get(index);
                ChatActivity.startMe(SearchActivity.this,new LinkMan(bean),SEARCH_TO_CHAT);
            }
        }

    }

}
