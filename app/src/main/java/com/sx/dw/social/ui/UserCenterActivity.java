package com.sx.dw.social.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.apkfuns.logutils.LogUtils;
import com.avos.avoscloud.feedback.FeedbackAgent;
import com.facebook.drawee.view.SimpleDraweeView;
import com.sx.dw.R;
import com.sx.dw.accountAndSecurity.entity.ActivityDetail;
import com.sx.dw.accountAndSecurity.ui.AccountAndSecurityActivity;
import com.sx.dw.accountAndSecurity.ui.LoginActivity;
import com.sx.dw.core.BaseActivity;
import com.sx.dw.core.DWApplication;
import com.sx.dw.core.GlobalData;
import com.sx.dw.core.util.C;
import com.sx.dw.im.entity.Chat;
import com.sx.dw.im.entity.ChatMsg;
import com.sx.dw.im.entity.LinkMan;
import com.sx.dw.im.message.MsgSyncListener;
import com.sx.dw.im.message.MsgSyncReceiverInActivity;
import com.sx.dw.im.message.MsgSyncService;
import com.sx.dw.test.TestFJActivity;
import com.sx.dw.version.UpdateHelper;
import com.sx.dw.videoChat.VideoChatActivity;
import com.sx.dw.videoChat.WebGameActivity;
import com.sx.dw.wealth.TokenUtil;
import com.sx.dw.wealth.WealthActivity;
import com.sx.dw.wealth.WealthHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import static com.sx.dw.core.GlobalData.accountInfo;
import static com.sx.dw.core.GlobalData.getClient;
import static com.sx.dw.core.GlobalData.getLatestActivity;
import static com.sx.dw.core.GlobalData.setAccountInfo;
import static com.sx.dw.core.util.C.RequestCode.CHAT_LIST_TO_CHAT;
import static com.sx.dw.core.util.C.RequestCode.LIKE_LIST_TO_CHAT;
import static com.sx.dw.core.util.C.RequestCode.LIKE_LIST_TO_USER_INFO;
import static com.sx.dw.core.util.C.RequestCode.RESET_INFO;
import static com.sx.dw.core.util.C.RequestCode.USER_CENTER_TO_SETTING_WIFI;
import static com.sx.dw.core.util.C.RequestCode.USER_CENTER_TO_VIDEO_CHAT;

/**
 * 用户中心页面
 */
public class UserCenterActivity extends BaseActivity {


    private CollapsingToolbarLayout toolbarLayout;
    private SimpleDraweeView sdvIcon;
    private ImageView ivStar;
    private ImageView ivSexIcon;
    private TextView tvWorth;
    private TextView tvWealth;
    private TextView tvName;
    private TextView tvSign;
    private TextView tvTag;
    private FloatingActionButton fabWallet;

    private ChatListFragment chatListFragment;
    private LikeListFragment likeListFragment;
    private List<Fragment> fragments;

    private MsgSyncReceiverInActivity msgSyncReceiver;
    private IntentFilter msgFilter;
    private Intent serviceIntent;

    private boolean isRefreshLikeList;
    private boolean isRefreshChatList;
    private MsgSyncListener msgSyncListener = new MsgSyncListener() {
        @Override
        public void onMsgGet(ChatMsg msg) {
            chatListFragment.refreshData();
        }
    };

    public static void startMe(Activity activity) {
        Intent intent = new Intent(activity, UserCenterActivity.class);
        activity.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_center);
        initView();
        FeedbackAgent agent = new FeedbackAgent(context);
        agent.sync();
        serviceIntent = new Intent(this, MsgSyncService.class);
        startService(serviceIntent);
        TokenUtil.getInstance().updateToken();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (msgSyncReceiver == null || msgFilter == null) {
            msgFilter = new IntentFilter();
            msgFilter.addAction(C.BROADCAST_ACTION_GET_MSG);
            msgFilter.setPriority(C.BROADCAST_PRIORITY_IN_ACTIVITY);
            msgSyncReceiver = new MsgSyncReceiverInActivity(msgSyncListener);
        }
        registerReceiver(msgSyncReceiver, msgFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        WealthHelper.getInstance().getUserWealth(tvWealth);
        setMyInfoPanel();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (isRefreshChatList) {
            chatListFragment.refreshData();
        }
        if (isRefreshLikeList) {
            likeListFragment.refreshData();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        setAccountInfo(accountInfo);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(msgSyncReceiver);
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        LinearLayout llUserInfoLayout = (LinearLayout) findViewById(R.id.ll_user_info_layout);
        sdvIcon = (SimpleDraweeView) findViewById(R.id.sdv_icon);
        ivStar = (ImageView) findViewById(R.id.iv_star);
        ivSexIcon = (ImageView) findViewById(R.id.iv_sex_icon);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvWorth = (TextView) findViewById(R.id.tv_worth);
        tvWealth = (TextView) findViewById(R.id.tv_wealth);
        tvSign = (TextView) findViewById(R.id.tv_sign);
        tvTag = (TextView) findViewById(R.id.tv_tag);
        fabWallet = (FloatingActionButton) findViewById(R.id.fab_wallet);
        appBarLayout.addOnOffsetChangedListener(onOffsetChangedListener);
        fabWallet.setOnClickListener(onClickListener);
        llUserInfoLayout.setOnClickListener(onClickListener);
        fragments = new ArrayList<>();
        chatListFragment = new ChatListFragment();
        likeListFragment = new LikeListFragment();
        fragments.add(chatListFragment);
        fragments.add(likeListFragment);
        viewPager.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager(), fragments));
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_user_center, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_account:
                startActivity(new Intent(this, AccountAndSecurityActivity.class));
                return true;
            case R.id.action_logout:
                logout();
                return true;
            case R.id.action_update:
                new UpdateHelper(this).checkUpdate(true);
                return true;
            case R.id.action_feedback:
                FeedbackAgent agent = new FeedbackAgent(context);
                agent.startDefaultThreadActivity();
                return true;
            case R.id.action_find:
                toVideoChat();
                return true;
            case R.id.action_test:
                TestFJActivity.startMe(this);
                return true;

            case R.id.to_search:
                toSearch();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.d("requestCode = " + requestCode + ",resultCode");
        switch (requestCode) {
            // FIXME: 2016/10/28 这里本来要通知fragment刷新数据，但少数情况下奔溃：
            // Failure delivering result ResultInfo{who=null, request=120, result=0, data=null}
            // 所以改变方式标记是否需要刷新后，在onPostResume方法中刷新
            case CHAT_LIST_TO_CHAT: {
                isRefreshChatList = true;
                break;
            }
            case LIKE_LIST_TO_CHAT: {
                isRefreshChatList = true;
                break;
            }
            case LIKE_LIST_TO_USER_INFO: {
                isRefreshChatList = true;
                isRefreshLikeList = true;
                break;
            }
            case USER_CENTER_TO_VIDEO_CHAT: {
                isRefreshLikeList = true;
                break;
            }
            case RESET_INFO: {
                if (resultCode == RESULT_OK) {
                    setMyInfoPanel();
                }
                break;
            }
            case USER_CENTER_TO_SETTING_WIFI: {
                toVideoChat();
                break;
            }
        }
    }

    private void startActivity(){
        ActivityDetail detail=getLatestActivity();
        Long current= Calendar.getInstance().getTimeInMillis();
        if(detail==null||detail.getBegintime()<=current){
//            WebGameActivity.startMe(this);
            VideoChatActivity.startMe(UserCenterActivity.this, USER_CENTER_TO_VIDEO_CHAT,detail);
        }else{
            //非活动时间进入网页欢迎页面
            WebWealcomeActivity.startMe(context,detail);
        }
    }


    private void toSearch(){
            SearchActivity.startMe(context);
    }
    private void toVideoChat() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info != null && info.getType() == ConnectivityManager.TYPE_WIFI) {
            startActivity();
        } else {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setMessage("您现在没有连接wifi，视频聊天将会消耗大量流量，确定继续吗？")
                    .setPositiveButton("我土豪", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity();
                        }
                    })
                    .setNegativeButton("去设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                            startActivityForResult(intent, USER_CENTER_TO_SETTING_WIFI);
                        }
                    })
                    .create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
    }

    private void setMyInfoPanel() {
        sdvIcon.setImageURI(accountInfo.getPrimaryIcon());
        if("腕".equals(accountInfo.getType())){
            ivStar.setVisibility(View.VISIBLE);
        }else {
            ivStar.setVisibility(View.GONE);
        }
        if (accountInfo.getSex() == 1) {
            ivSexIcon.setImageResource(R.drawable.icon_sex_man_x3);
        } else if (accountInfo.getSex() == 2) {
            ivSexIcon.setImageResource(R.drawable.icon_sex_wuman_x3);
        }
        setTextView(accountInfo.getName(), tvName);
        setTextView(accountInfo.getWorthString(), tvWorth);
        setTextView(accountInfo.getWealthString(), tvWealth);
        if(accountInfo.getSign()!=null&&accountInfo.getSign().length()>20){
            setTextView(accountInfo.getSign().substring(0,20)+"...", tvSign);
        }else{
            setTextView(accountInfo.getSign(), tvSign);
        }
        setTextView(accountInfo.getTag(), tvTag);
    }

    private void setTextView(String text, TextView textView) {
        if (!TextUtils.isEmpty(text)) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(text);
        } else {
            textView.setVisibility(View.INVISIBLE);
        }
    }


    /**
     * 退出登录
     */
    private void logout() {
        showLoading();
        getClient().logout(true);
        setAccountInfo(null);
        TokenUtil.getInstance().clearToken();
        JPushInterface.setAlias(this, "", new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {

            }
        });
        JPushInterface.setTags(context, null, new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {

            }
        });
        List<Chat> chats = new Select().from(Chat.class).execute();
        List<LinkMan> linkMans = new Select().from(LinkMan.class).execute();
        List<ChatMsg> chatMsgs = new Select().from(ChatMsg.class).execute();
        for (LinkMan m : linkMans) {
            m.delete();
        }
        for (ChatMsg m : chatMsgs) {
            m.delete();
        }
        for (Chat m : chats) {
            m.delete();
        }
        stopService(serviceIntent);
        GlobalData.updateChats();
        GlobalData.updateLinkMans();
        DWApplication.exitApp();
        startActivity(new Intent(this, LoginActivity.class));
        dismissLoading();
    }


    private AppBarLayout.OnOffsetChangedListener onOffsetChangedListener = new AppBarLayout.OnOffsetChangedListener() {
        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
            if (verticalOffset == 0) {
                fabWallet.setVisibility(View.VISIBLE);
                toolbarLayout.setTitle("");

            } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                fabWallet.setVisibility(View.GONE);
                toolbarLayout.setTitle(accountInfo.getName());
            } else {
                fabWallet.setVisibility(View.VISIBLE);
                toolbarLayout.setTitle("");
            }
        }

    };

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.fab_wallet: {
                    startActivity(new Intent(UserCenterActivity.this, WealthActivity.class));
                    break;
                }
                case R.id.ll_user_info_layout: {
                    SetUserInfoActivity.startMe(UserCenterActivity.this, RESET_INFO);
                    break;
                }
            }
        }
    };



    public static class SectionsPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments;

        SectionsPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "消息";
                case 1:
                    return "收藏";
            }
            return null;
        }
    }
}
