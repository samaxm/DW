package com.sx.dw.social.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.sx.dw.R;
import com.sx.dw.core.BaseActivity;
import com.sx.dw.core.util.C;
import com.sx.dw.im.entity.LinkMan;
import com.sx.dw.im.ui.ChatActivity;

import java.util.LinkedList;
import java.util.List;

import static com.sx.dw.core.GlobalData.accountInfo;
import static com.sx.dw.core.util.C.IntentExtra.LINKMAN;
import static com.sx.dw.core.util.C.IntentExtra.REQUEST_CODE;
import static com.sx.dw.core.util.C.RequestCode.LIKE_LIST_TO_CHAT;

/**
 * 用户信息页面
 */
public class UserInfoActivity extends BaseActivity {


    private ViewPager sdvUserIcon;

    private TextView tvUserName;
    private TextView tvUserNameTitle;
    private TextView tvUserSign;
    private TextView tvUserWorth;
    private TextView tvUserArea;
    private ImageView tvUserSex;
    private ImageView starLogo;
    private UserIconPageAdapter adapter;

    private ImageView sendMsg;
    private TextView tvrName;
    private TextView tvCompany;
    private TextView tvJobTitle;
    private TextView star;
    private TextView tagText;
    private TextView age;

    private ImageView isLiked;
    private Handler showIconHandler=new Handler();
    private Runnable showIconRunnable=new Runnable() {
        @Override
        public void run() {
            sdvUserIcon.setCurrentItem((sdvUserIcon.getCurrentItem()+1)%adapter.getCount());
            showIconHandler.postDelayed(this,2000);
        }
    };

    private LinkMan linkMan;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    public static void startMe(Activity activity, LinkMan entity, int requestCode) {
        Intent intent = new Intent(activity, UserInfoActivity.class);
        intent.putExtra(LINKMAN, entity);
        intent.putExtra(REQUEST_CODE, requestCode);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        linkMan = (LinkMan) getIntent().getSerializableExtra(LINKMAN);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initView();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        showIconHandler.postDelayed(showIconRunnable,2000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_info, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_unlike) {

        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        sdvUserIcon= (ViewPager) findViewById(R.id.icon_pagers);
        List<String> icons=new LinkedList<>();
        if (linkMan.getIcon() != null&&!linkMan.getIcon().equals("")) {
            String[] arr=linkMan.getIcon().split(";");
            for(String icon:arr){
                icons.add(icon);
            }
//            sdvUserIcon.setImageURI(linkMan.getIcon());
//            sdvUserIcon.setImageURI(linkMan.getIcon());
        }
        adapter=new UserIconPageAdapter(getSupportFragmentManager(),icons,sdvUserIcon,null);
        sdvUserIcon.setAdapter(adapter);

        sdvUserIcon.addOnPageChangeListener(adapter);
        tvUserName = (TextView) findViewById(R.id.tv_user_name);
        tvUserSign = (TextView) findViewById(R.id.tv_user_sign);
        tvUserWorth = (TextView) findViewById(R.id.tv_user_worth);
        tvUserSex = (ImageView) findViewById(R.id.tv_user_sex);
        starLogo= (ImageView) findViewById(R.id.info_starLogo);
        tagText= (TextView) findViewById(R.id.info_tag);
        age = (TextView) findViewById(R.id.tv_user_age);
        star = (TextView) findViewById(R.id.tv_user_star);
        tvrName = (TextView) findViewById(R.id.tv_user_rname);
        tvCompany= (TextView) findViewById(R.id.tv_user_company);
        tvJobTitle= (TextView) findViewById(R.id.tv_user_title);

        tvUserArea = (TextView) findViewById(R.id.tv_user_area);
        sendMsg = (ImageView) findViewById(R.id.tv_send_msg);
        tvUserNameTitle = (TextView) findViewById(R.id.name_title);
        isLiked = (ImageView) findViewById(R.id.isLike);



        age.setText(linkMan.getAge());
        star.setText(linkMan.getStar());
        tvrName.setText(linkMan.getRname());
        tvCompany.setText(linkMan.getCompany());
        tvJobTitle.setText(linkMan.getTitle());
        tvUserName.setText(linkMan.getName());
        tvUserNameTitle.setText(linkMan.getName());
        tvUserSign.setText(linkMan.getSign());
        tvUserWorth.setText(linkMan.getWorthString());
        if(C.STAR.equals(linkMan.getType())){
            if(linkMan.getTag()!=null) {
                tagText.setText(linkMan.getTag());
                tagText.setVisibility(View.VISIBLE);
            }
            starLogo.setVisibility(View.VISIBLE);
        }


        if ("男".equals(linkMan.getSexString())) {
            tvUserSex.setImageResource(R.drawable.male_icon);
        } else {
            tvUserSex.setImageResource(R.drawable.female);
        }

        if (!linkMan.isLike()) {
            isLiked.setImageResource(R.drawable.action_icon_like);
        }

        tvUserArea.setText(linkMan.getArea());

        if (linkMan.getDwID().equals(accountInfo.getId())) {
            sendMsg.setVisibility(View.GONE);
            isLiked.setVisibility(View.GONE);
        }

    }

    public void sendMsg(View view) {
        ChatActivity.startMe(this, linkMan, LIKE_LIST_TO_CHAT);
        finish();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("UserInfo Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
