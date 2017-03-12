package com.sx.dw.videoChat;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.apkfuns.logutils.LogUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hyphenate.chat.EMCallManager;
import com.hyphenate.chat.EMCallStateChangeListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.EMNoActiveCallException;
import com.hyphenate.exceptions.EMServiceNotReadyException;
import com.hyphenate.media.EMLocalSurfaceView;
import com.hyphenate.media.EMOppositeSurfaceView;
import com.sx.dw.R;
import com.sx.dw.accountAndSecurity.entity.ActivityDetail;
import com.sx.dw.accountAndSecurity.entity.AnswerResponse;
import com.sx.dw.accountAndSecurity.entity.WebGame;
import com.sx.dw.accountAndSecurity.helper.AppApi;
import com.sx.dw.core.AppConfig;
import com.sx.dw.core.BaseActivity;
import com.sx.dw.core.GlobalData;
import com.sx.dw.core.network.DwCallback;
import com.sx.dw.core.network.DwRetrofit;
import com.sx.dw.core.network.EntityHead;
import com.sx.dw.core.util.C;
import com.sx.dw.core.util.ToastUtil;
import com.sx.dw.im.entity.LinkMan;
import com.sx.dw.social.UserInfoApi;
import com.sx.dw.social.ui.UserCenterActivity;
import com.sx.dw.wealth.WealthApi;
import com.sx.dw.wealth.WealthHelper;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.sx.dw.core.GlobalData.accountInfo;
import static com.sx.dw.core.GlobalData.getCallManager;
import static com.sx.dw.core.GlobalData.inChat;
import static com.sx.dw.core.GlobalData.inChatRoom;
import static com.sx.dw.core.GlobalData.setAccountInfo;


/**
 * 视频聊天页面
 */
// FIXME: 2016/11/14 卡画面是因为surfaceView里还保留通话结束时的画面，又没有新的绘制任务所以显示上一帧的画面
public class VideoChatActivity extends BaseActivity {

    private static final int MSG_MAKE_CALL = 101;
    private static final int MSG_ANSWER_CALL = 102;
    private static final int MSG_END_CALL = 103;
    private static final int INIT = 104;

    /* 视图组件域 */
    private WebView webGameView;
    private SimpleDraweeView ivUserIcon;
    private TextView tvTitle;
    private EMOppositeSurfaceView svBigVideo;
    private RecyclerView rvJokeList;
    private TextView tvUserSign;
    private EMLocalSurfaceView svMiniVideo;
    private TextView tvWealth;
    private ImageView toMessage;
    private ImageView ivChangeCamera;
    //    private ImageView ivChangeHorn;
    private TextView ivLikeUser;
    private ImageView ivNextOne;
    private ImageView ivTip;
    private ImageView starLogo;
    private TextView starTag;
    private EditText gift_et;
    private EditText address_et;
    private View signArea;
    private View pop;
    private TextView confirm_address;
    /* 核心域 */
    private EMCallManager callManager;
    private VideoChatReceiver emReceiver;
    private IntentFilter emReceiverFilter;
    private VideoChatHelper videoChatHelper;
    private UserInfoApi userInfoApi;
    private AppApi appApi;

    private ActivityDetail currentActivity;

    /*其他类域*/
    private AudioManager audioManager;
    private LinkMan inChatLinkMan;
    private String inChatLinkManId;
    private TipReceiver tipReceiver;
    private IntentFilter tipReceiverFilter;
    private int requestCode;
    private boolean chatEnding;
    private boolean isLocalSmall=true;

    public static String UNLIKED="收藏ta";
    public static String LIKED="已收藏";

    private Runnable changeColorRun=new Runnable() {
        @Override
        public void run() {

            if(ivLikeUser.getHint().toString().equals("deep")){
                ivLikeUser.setHint("color");
                ivLikeUser.setTextColor(ContextCompat.getColor(context,R.color.colorPrimary));
                ivLikeUser.setBackground(ContextCompat.getDrawable(context,R.drawable.rounded_bg_red));
            }else{
                ivLikeUser.setHint("deep");
                ivLikeUser.setTextColor(ContextCompat.getColor(context,R.color.avoscloud_feedback_white));
                ivLikeUser.setBackground(ContextCompat.getDrawable(context,R.drawable.rounded_bg_red_deep));
            }
            changeLikeBtn.postDelayed(this,300);
        }
    };
    private Handler changeLikeBtn=new Handler();

    private boolean isFinished;
    private Integer answerID;
    public static void startMe(Activity activity, int requestCode,ActivityDetail currentActivity) {
        Intent intent=ActivityDetail.createInentWithActivity(activity,VideoChatActivity.class,currentActivity);
        intent.putExtra(C.IntentExtra.REQUEST_CODE, requestCode);
        activity.startActivityForResult(intent, requestCode);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.v("onCreate");
        setContentView(R.layout.activity_video_layout);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        requestCode = getIntent().getIntExtra(C.IntentExtra.REQUEST_CODE, -99);
        currentActivity=ActivityDetail.getActivityDetail(getIntent());


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        initView();
        initHelper();

        ToastUtil.debugToast("isConnected():" + EMClient.getInstance().isConnected());
        if (tipReceiver == null || tipReceiverFilter == null) {
            tipReceiver = new TipReceiver(tipCallback);
            tipReceiverFilter = new IntentFilter(C.BROADCAST_ACTION_TIP);
        }
        registerReceiver(tipReceiver, tipReceiverFilter);
    }

    private void initView() {
        webGameView= (WebView) findViewById(R.id.gamepage);
        webGameView.setWebViewClient(new WebViewClient());
        //得到webview设置
        WebSettings webSettings = webGameView.getSettings();
        //允许使用javascript
        webSettings.setJavaScriptEnabled(true);
        ivUserIcon = (SimpleDraweeView) findViewById(R.id.iv_user_icon);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvUserSign = (TextView) findViewById(R.id.tv_user_sign);
        svBigVideo = (EMOppositeSurfaceView) findViewById(R.id.sv_big_video);
        svMiniVideo = (EMLocalSurfaceView) findViewById(R.id.sv_mini_video);
        svMiniVideo.setZOrderMediaOverlay(true);
        signArea=findViewById(R.id.tv_user_tag);
        rvJokeList = (RecyclerView) findViewById(R.id.rv_joke_list);
        toMessage= (ImageView) findViewById(R.id.to_message);
        starLogo= (ImageView) findViewById(R.id.tag_starLogo);
        starTag= (TextView) findViewById(R.id.tv_user_tag_text);
        tvWealth = (TextView) findViewById(R.id.tv_wealth);
        ivChangeCamera = (ImageView) findViewById(R.id.iv_change_camera);
//        ivChangeHorn = (ImageView) findViewById(R.id.iv_change_horn);
        ivLikeUser = (TextView) findViewById(R.id.iv_like_user);
        ivNextOne = (ImageView) findViewById(R.id.iv_next_one);
        ivTip = (ImageView) findViewById(R.id.iv_tip);


        toMessage.setOnClickListener(onClickListener);
        svMiniVideo.setOnClickListener(onClickListener);
        svBigVideo.setOnClickListener(onClickListener);
        tvWealth.setText(accountInfo.getWealthString());
        ivChangeCamera.setOnClickListener(onClickListener);
//        ivChangeHorn.setOnClickListener(onClickListener);
        ivLikeUser.setOnClickListener(onClickListener);
        ivNextOne.setOnClickListener(onClickListener);
        ivTip.setOnClickListener(onClickListener);
        if(currentActivity!=null){
            pop=findViewById(R.id.pop);
            confirm_address= (TextView) findViewById(R.id.confirm_btn);
            address_et= (EditText) findViewById(R.id.address_et);
            confirm_address.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    uploadAddress();
                }
            });
            gift_et= (EditText) findViewById(R.id.gift_et);
            gift_et.setVisibility(View.VISIBLE);
            address_et.setCursorVisible(false);
            gift_et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEND
                            || actionId == EditorInfo.IME_ACTION_DONE
                            || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
                        //处理事件
                        sendAnswer();
                    }
                    return false;
                }
            });
        }
    }

    private void initHelper() {
        callManager =getCallManager();
        videoChatHelper = new VideoChatHelperImpl(this, rvJokeList, matchCallback);
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        try{
            audioManager.setBluetoothScoOn(true);
            if(!audioManager.isSpeakerphoneOn()) {
                audioManager.setSpeakerphoneOn(true);
                audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                        audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL ),
                        AudioManager.STREAM_VOICE_CALL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        userInfoApi = DwRetrofit.getInstance().createApi(UserInfoApi.class);
        appApi = DwRetrofit.getInstance().createApi(AppApi.class);
        appApi.getWebGame().enqueue(new Callback<EntityHead<WebGame>>() {
            @Override
            public void onResponse(Call<EntityHead<WebGame>> call, Response<EntityHead<WebGame>> response) {
                if(response!=null&&response.isSuccessful()&&response.body()!=null
                        &&response.body().getData()!=null&&response.body().getData().getUrl()!=null){
                    webGameView.loadUrl(response.body().getData().getUrl());
                }else
                    destroyWebView();
            }
            @Override
            public void onFailure(Call<EntityHead<WebGame>> call, Throwable t) {
                destroyWebView();
            }
        });
    }

    @Override
    protected void onRestart() {
        LogUtils.v("onRestart");
        super.onRestart();
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtils.v("onStart");
        if (!inChat) {
            callManager.addCallStateChangeListener(emCallStateChangeListener);
        }
//        动态注册来电广播接收器
        if (emReceiver == null || emReceiverFilter == null) {
            emReceiver = new VideoChatReceiver(callInCallback);
            emReceiverFilter = new IntentFilter(callManager.getIncomingCallBroadcastAction());
        }
        registerReceiver(emReceiver, emReceiverFilter);
//        changeHorn(true);
        videoChatHelper.startPing();

    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtils.v("onResume,inChat = " + inChat);
        isFinished=false;
        callManager.setSurfaceView(svMiniVideo,svBigVideo);
        /* 启动工作 */
        GlobalData.inChatRoom = true;
        if (!inChat) {
            videoChatHelper.startMatch(false);
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    private void uploadAddress(){
        String address=address_et.getText().toString();
        if(address==null||address.equals("")||answerID==null){
            return;
        }
        address_et.getText().clear();
        View view = this.getCurrentFocus();
        if(view!=null){
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        pop.setVisibility(View.GONE);
        appApi.uploadAddress(answerID,accountInfo.getId(),address).enqueue(new Callback<EntityHead<AnswerResponse>>() {
            @Override
            public void onResponse(Call<EntityHead<AnswerResponse>> call, Response<EntityHead<AnswerResponse>> response) {
                if(response.body()!=null&&response.body().isSuccess()) {
                    ToastUtil.showToast("等待"+response.body().getData().getGift()+"到你家吧！^_^");
                }
            }
            @Override
            public void onFailure(Call<EntityHead<AnswerResponse>> call, Throwable t) {
                    ToastUtil.showToast("抱歉，地址上传失败，请联系客服");
            }
        });
    }

    private void sendAnswer(){
        String answer=gift_et.getText().toString();
        if(answer==null||answer.equals("")){
            return;
        }
        answer=answer.trim();
        answer=answer.replaceAll("\n","");
        gift_et.getText().clear();
        gift_et.setHint(R.string.edittext_hint);
        gift_et.clearFocus();
        View view = this.getCurrentFocus();
        if(view!=null){
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        appApi.checkActivityAnswer(accountInfo.getId(), answer, currentActivity.getId()).enqueue(new Callback<EntityHead<AnswerResponse>>() {
            @Override
            public void onResponse(Call<EntityHead<AnswerResponse>> call, Response<EntityHead<AnswerResponse>> response) {
                if (response.body()!=null && response.body().isSuccess()) {
                    AnswerResponse answerResponse = response.body().getData();
                    answerID=answerResponse.getId();
                    if (pop!=null)
                        pop.setVisibility(View.VISIBLE);
                }else{
                    ToastUtil.showToast("抱歉哦，你的暗号错了..");
                }
            }
            @Override
            public void onFailure(Call<EntityHead<AnswerResponse>> call, Throwable t) {
                ToastUtil.showToast("抱歉哦，你的暗号错了..");
            }
        });
    }

    private void changeView(){
        if(!inChat){
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(isLocalSmall){
                    ViewGroup bigArea= (ViewGroup) findViewById(R.id.big_area);
                    ViewGroup smallArea= (ViewGroup) findViewById(R.id.fl_mini_video);
                    smallArea.removeView(svMiniVideo);
                    bigArea.removeView(svBigVideo);

                    svMiniVideo.setZOrderMediaOverlay(false);
                    svBigVideo.setZOrderMediaOverlay(true);

                    ViewGroup.LayoutParams params=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
                    bigArea.addView(svMiniVideo,0,params);
                    smallArea.addView(svBigVideo,0,params);
                    isLocalSmall=false;
                }else{
                    ViewGroup bigArea= (ViewGroup) findViewById(R.id.big_area);
                    ViewGroup smallArea= (ViewGroup) findViewById(R.id.fl_mini_video);
                    smallArea.removeView(svBigVideo);
                    bigArea.removeView(svMiniVideo);
                    svMiniVideo.setZOrderMediaOverlay(true);
                    svBigVideo.setZOrderMediaOverlay(false);

                    ViewGroup.LayoutParams params=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
                    bigArea.addView(svBigVideo,0,params);
                    smallArea.addView(svMiniVideo,0,params);
                    isLocalSmall=true;
                }
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        LogUtils.v("onPause");
        videoChatHelper.stopMatch();
        GlobalData.inChatRoom = false;
        videoChatHelper.removeMatch();
        handler.sendEmptyMessage(MSG_END_CALL);

    }


    @Override
    protected void onStop() {
        super.onStop();
        LogUtils.v("onStop");
        try {
            unregisterReceiver(emReceiver);
        } catch (IllegalArgumentException e) {
            LogUtils.w(e);
        }
        videoChatHelper.stopPing();
        videoChatHelper.setIsPrioritized(false);
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        finishActivity();
    }

    private void finishActivity(){
        if(isFinished){
            return;
        }
        videoChatThread.quit();
        if (emCallStateChangeListener != null) {
            callManager.removeCallStateChangeListener(emCallStateChangeListener);
        }

        try{
            callManager.endCall();
        }catch (EMNoActiveCallException e){
            try {
                //傻逼环信没法主动释放摄像头，只能调用endCall
                callManager.makeVideoCall("123");
                callManager.endCall();
            } catch (Exception ex) {
            }
            LogUtils.e("e",e);
        }catch (Exception e){
        }
        videoChatThread=null;


        callManager = null;

        svBigVideo = null;
        svMiniVideo = null;
        matchCallback = null;
        unregisterReceiver(tipReceiver);
        LogUtils.v("onDestroy");
        isFinished=true;
        inChat=false;
    }

    @Override
    public void onBackPressed() {
        if (!inChat) {
            super.onBackPressed();
            return;
        }
        showExitDialog(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                VideoChatActivity.super.onBackPressed();
            }
        });
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_video_chat, menu);
//        itemCollect = menu.findItem(R.id.action_like);
//        itemReport = menu.findItem(R.id.action_report);
//        return super.onCreateOptionsMenu(menu);
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_like:
//                if (inChat && inChatLinkMan != null) {
////                    videoChatHelper.like(inChatLinkMan.getDwID(),item);
//                }
//                return true;
//            case R.id.action_to_user_center:
//                if (!inChat) {
//                    if (requestCode == C.RequestCode.LOADING_TO_VIDEO_CHAT) {
//                        UserCenterActivity.startMe(VideoChatActivity.this);
//                    }
//                    finish();
//                    return true;
//                }
//                showExitDialog(new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        exitChatRoom();
//                    }
//                });
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    private void callThisGuy(LinkMan linkMan) {
        ToastUtil.debugToast("callThisGuy:" + linkMan.getDwID());
        if (!GlobalData.inChatRoom || callManager == null) {
            return;
        }
        String userId = linkMan.getDwID();
        LogUtils.d(" username = " + userId);
        inChatLinkManId = userId;
        handler.sendEmptyMessage(MSG_MAKE_CALL);
    }

    private void setUserInfo() {
        if (inChatLinkMan == null) {
            if (!TextUtils.isEmpty(inChatLinkManId)) {
                getUserInfo(inChatLinkManId);
            } else {
                tvTitle.setText("游客");
            }
            return;
        }

        tvTitle.setText(inChatLinkMan.getName());
        ivLikeUser.setVisibility(View.VISIBLE);
        String sign = inChatLinkMan.getSign();
        if (!TextUtils.isEmpty(sign)) {
            signArea.setVisibility(View.VISIBLE);
            tvUserSign.setText(sign);
        }
        if(inChatLinkMan.getTag()!=null&&(C.STAR.equals(inChatLinkMan.getType())||inChatLinkMan.getType()==null)){
            starLogo.setVisibility(View.VISIBLE);
            if(inChatLinkMan.getTag()!=null){
                starTag.setText(inChatLinkMan.getTag());
                starTag.setVisibility(View.VISIBLE);
            }
        }else{
            starLogo.setVisibility(View.GONE);
            starTag.setVisibility(View.GONE);
        }
        LinkMan linkMan = GlobalData.linkManMap.get(inChatLinkMan.getDwID());
        if (linkMan != null && linkMan.isLike()) {
            ivLikeUser.setText(LIKED);
        }else{
            changeLikeBtn.postDelayed(changeColorRun,300);
        }

        ivUserIcon.setImageURI(inChatLinkMan.getPrimaryIcon());
        ivUserIcon.setVisibility(View.VISIBLE);


    }

    private void getUserInfo(String from) {
        userInfoApi.getUserInfo(from).enqueue(new DwCallback<EntityHead<LinkMan>>() {
            @Override
            public void getBody(Call call, EntityHead<LinkMan> body) {
                if (body.isSuccess()) {
                    inChatLinkMan = body.getData();
                    if (inChatLinkMan != null) {
                        setUserInfo();
                    } else {
                        tvTitle.setText("游客");
                    }
                } else {
                    ToastUtil.showToast(body.getMsg());
                }
            }
        });
    }

//    private void changeHorn(boolean isOn) {
//        audioManager.setSpeakerphoneOn(isOn);
//        if (isOn) {
//            ivChangeHorn.setImageResource(R.drawable.action_icon_horn_on_x3);
//        } else {
//            ivChangeHorn.setImageResource(R.drawable.action_icon_horn_off_x3);
//        }
//    }

    private void endChat() {
        destroyWebView();
        endChat(null);
    }

    //  优化挂断卡顿问题 子线程和UI线程异步执行 挂断操作、ui刷新，挂断完成后跳回UI执行surface刷新
    private void endChat(final String toast) {
        if (chatEnding) {
            return;
        }
        chatEnding = true;
        ToastUtil.debugToast("endChat");
        handler.sendEmptyMessage(MSG_END_CALL);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!isFinishing()) {
                   initChatUI(toast);
                }
            }
        });

        inChatLinkMan = null;
        videoChatHelper.startMatch(true);
    }

    private void endCall() {
        if (inChat) {
            inChat = false;
            try {
                callManager.endCall();

            } catch (Exception e) {
                LogUtils.e("endCall:" + e);
            }
        }
        if (!isFinishing()) {
            initVideoView();
            chatEnding = false;
        }
    }

    private void initChatUI(String toast) {
        ToastUtil.showToast(toast);
        tvTitle.setText("继续寻找");
        ivLikeUser.setVisibility(View.INVISIBLE);
        ivNextOne.setVisibility(View.INVISIBLE);
        ivTip.setVisibility(View.INVISIBLE);
        ivUserIcon.setVisibility(View.GONE);
        signArea.setVisibility(View.GONE);
//        svBigVideo.setBackgroundResource(R.drawable.video_background);
        svBigVideo.setVisibility(View.GONE);

    }

    //    最后执行这步
    private void initVideoView() {
       handler.sendEmptyMessage(INIT);
    }

    private void beginChat() {
        destroyWebView();
        if (!inChatRoom) {
            return;
        }
        LogUtils.v("beginChat");
        videoChatHelper.setIsPrioritized(false);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setUserInfo();
                inChat = true;
                ivNextOne.setVisibility(View.VISIBLE);
                ivTip.setVisibility(View.VISIBLE);
//                svBigVideo.setBackgroundColor(Color.TRANSPARENT);
                svBigVideo.setVisibility(View.VISIBLE);
                videoChatHelper.stopMatch();
            }
        });

    }

    private void showExitDialog(DialogInterface.OnClickListener onClickListener) {
        new AlertDialog.Builder(this)
                .setTitle("温馨提示")
                .setMessage("离开视频界面将结束视频聊天，继续吗？")
                .setPositiveButton("确定", onClickListener)
                .setNegativeButton("取消", null)
                .show();
    }



   private void exitChatRoom() {
        exitChatRoom(null);
    }

    private void exitChatRoom(String toast) {
        if (!TextUtils.isEmpty(toast)) {
            ToastUtil.showToast(toast);
        }
        if (requestCode == C.RequestCode.LOADING_TO_VIDEO_CHAT) {
            UserCenterActivity.startMe(VideoChatActivity.this);
        }
        finish();
    }

    public void testSomething(View view) {
        if (!AppConfig.IS_DEBUG) {
            return;
        }
        initVideoView();
    }

    private TipReceiver.TipCallback tipCallback = new TipReceiver.TipCallback() {
        @Override
        public void getTip(int amount) {
            ToastUtil.showToast("收到打赏 " + amount + " 大洋！");
            WealthHelper.getInstance().getUserWealth(tvWealth);
        }

    };

    private HandlerThread videoChatThread = new HandlerThread("videoChatThread");{
        videoChatThread.start();
    }



    private Handler handler = new Handler(videoChatThread.getLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_MAKE_CALL: {
                    try {
                        callManager.makeVideoCall(inChatLinkManId);
                    } catch (EMServiceNotReadyException e) {
                        LogUtils.e("makeVideoCall: " + e);
                        exitChatRoom("网络异常，请重试！");
                    }
                    break;
                }
                case MSG_ANSWER_CALL: {
                    try {
                        callManager.answerCall();
                    } catch (EMNoActiveCallException e) {
                        LogUtils.e("answerCall:" + e);
                    }
                    break;
                }
                case MSG_END_CALL: {
                    endCall();
                    break;
                }
                case INIT: {
                    callManager.setSurfaceView(svMiniVideo, svBigVideo);
                    break;
                }
            }
        }
    };

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.sv_mini_video: {
                    if(isLocalSmall){
                        initVideoView();
                        break;
                    }else{
                        changeView();
                        break;

                    }

                }
                case R.id.sv_big_video: {
                    if(isLocalSmall){
                        changeView();
                        break;
                    }else{
                        initVideoView();
                        break;
                    }

                }
                case R.id.iv_change_camera: {
                    callManager.switchCamera();
                    break;
                }
                case R.id.to_message:{
                    showExitDialog(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (requestCode == C.RequestCode.LOADING_TO_VIDEO_CHAT) {
                                UserCenterActivity.startMe(VideoChatActivity.this);
                            }
                            finish();
                        }
                    });
                    break;
                }
//                case R.id.iv_change_horn: {
////                切换通话喇叭和按钮状态
//                    changeHorn(!audioManager.isSpeakerphoneOn());
//                    break;
//                }
                case R.id.iv_next_one: {
                    if (inChat) {
                        endChat();
                    }
                    break;
                }
                case R.id.iv_like_user: {
                    if (ivLikeUser.getText().toString().equals(LIKED)) {
                        break;
                    }
                    if (inChatLinkMan != null) {
                        videoChatHelper.like(inChatLinkMan.getDwID(), ivLikeUser);
                    } else if (!TextUtils.isEmpty(inChatLinkManId)) {
                        videoChatHelper.like(inChatLinkManId, ivLikeUser);
                    }
                    changeLikeBtn.removeCallbacks(changeColorRun);
                    break;
                }
                case R.id.iv_tip: {
                    if (GlobalData.inChat) {
                        sendTip();
                    }
                    break;
                }
//                case gift_btn:{
//                    showGiftConfirmDialog();
//                    }
            }
        }
    };

//    private void checkPayPsd() {
//        if (accountInfo.isUserCanWithdraw()) {
//            showTipDialog();
//        } else {
//            ToastUtil.showToast("您还未设置支付密码，请先到财富页面设置支付密码哦");
//        }
//    }

//    private void showTipDialog() {
//        View root = LayoutInflater.from(this).inflate(R.layout.dialog_input_pay_psd, null);
//        final EditText editText = (EditText) root.findViewById(R.id.et_input_pay_psd);
//        new AlertDialog.Builder(this)
//                .setView(root)
//                .setTitle("验证支付密码")
//                .setMessage("打赏对方100个大洋")
//                .setPositiveButton("验证", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        boolean attemptInput = InputAttempt.attemptInput(editText, InputAttempt.getPayPsdRule());
//                        if (attemptInput) {
//                            String psd = editText.getText().toString();
//                            sendTip(AES.encode(MD5.GetMD5Code(psd), TokenUtil.getInstance().getKey()));
//                        }
//                    }
//                }).show();
//    }

    private void sendTip() {
        int count = AppConfig.IS_DEBUG?10:100;
        DwRetrofit.getInstance().createApi(WealthApi.class)
                .tip(accountInfo.getId(), inChatLinkManId,count)
                .enqueue(new DwCallback<EntityHead<TipResultEntity>>() {
                    @Override
                    public void getBody(Call call, EntityHead<TipResultEntity> body) {
                        if (body.isSuccess()) {
                            ToastUtil.showToast("打赏成功！");
                            accountInfo.setWealth(body.getData().getWealth());
                            setAccountInfo();
                            tvWealth.setText(accountInfo.getWealthString());
                        } else {
                            ToastUtil.showToast(body.getMsg());
                        }

                    }
                });
    }

    private VideoChatHelperImpl.MatchCallback matchCallback = new VideoChatHelperImpl.MatchCallback() {
        @Override
        public void onSuccess(LinkMan linkMan) {
            tvTitle.setText("正在连接");
            inChatLinkMan = linkMan;
            callThisGuy(linkMan);
            //end  game
            destroyWebView();
            LogUtils.d(linkMan);
        }

        @Override
        public void onMatching() {
            tvTitle.setText("正在寻找，看看段子");
        }

        @Override
        public void onFailure(String msgToUser) {
            tvTitle.setText("");
            ToastUtil.showToast(msgToUser);
        }
    };


    private void destroyWebView() {
        if(webGameView != null) {
            webGameView.setVisibility(View.GONE);
            webGameView.clearHistory();
            webGameView.clearCache(true);
            webGameView.loadUrl("about:blank"); // clearView() should be changed to loadUrl("about:blank"), since clearView() is deprecated now
            webGameView.pauseTimers();
//            webGameView.destroy();
            webGameView = null; // Note that mWebView.destroy() and mWebView = null do the exact same thing
        }

    }
    private VideoChatReceiver.CallInCallback callInCallback = new VideoChatReceiver.CallInCallback() {
        @Override
        public void callIn(String from) {
            ToastUtil.debugToast("callIn: " + from);
            LogUtils.d("from = " + from + ",thread = " + Thread.currentThread().getName());
            if (!GlobalData.inChatRoom) {
                return;
            }
            inChatLinkManId = from;
            handler.sendEmptyMessage(MSG_ANSWER_CALL);

        }
    };

    private EMCallStateChangeListener emCallStateChangeListener = new EMCallStateChangeListener() {
        @Override
        public void onCallStateChanged(CallState callState, CallError callError) {
            LogUtils.v("callState = " + callState + ", callError = " + callError);
            try{
                switch (callState) {
                    case ACCEPTED: {
                        beginChat();
                        break;
                    }
                    case DISCONNNECTED: {
//                    if(callError == CallError.ERROR_BUSY){
//                        exitChatRoom("网络异常，请重试！");
//                        break;
//                    }
                        if (inChat) {
                            endChat();
                        }
                        break;
                    }
                    case NETWORK_UNSTABLE: {
                        if (inChat) {
                            endChat("网络不稳定");
                        }
                        break;
                    }
                }
            }catch (Exception e){
                Writer writer = new StringWriter();
                PrintWriter printWriter = new PrintWriter(writer);
                e.printStackTrace(printWriter);
                String s = writer.toString();
                ToastUtil.showToast(s);
            }
        }
    };

}
