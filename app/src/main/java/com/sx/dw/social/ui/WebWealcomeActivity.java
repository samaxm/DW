package com.sx.dw.social.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.sx.dw.R;
import com.sx.dw.accountAndSecurity.entity.ActivityDetail;
import com.sx.dw.accountAndSecurity.ui.LoadingActivity;
import com.sx.dw.core.BaseActivity;

import java.util.Calendar;

import static com.sx.dw.accountAndSecurity.entity.ActivityDetail.ActivityBundle;
import static com.sx.dw.accountAndSecurity.entity.ActivityDetail.ActivityKey;
import static com.sx.dw.core.GlobalData.accountInfo;

/**
 * @Description: TODO 添加类的描述
 * @author: fanjie
 * @date: 2017/1/4 10:35
 */

public class WebWealcomeActivity extends BaseActivity {
    private WebView myWebView ;
    private ImageView toMessage;
    private TextView textView;
    private ActivityDetail currentActivity;

    private static long HourLen=60*60*1000;
    private static long MinuteLen=60*1000;
    private static long SencondLen=1000;
    private static String prefix="离活动还有：";

    private Handler handler=new Handler();
    private Runnable changeTime=new Runnable() {
        @Override
        public void run() {
            String time=getLeftTime();
            if(time!=null) {
                textView.setText(time);
                handler.postDelayed(this, 1000);
            }else{
                LoadingActivity.startMe(context);
                finish();
            }
        }
    };

    private String getLeftTime(){
        long beginTime=currentActivity.getBegintime();
        long current= Calendar.getInstance().getTimeInMillis();
        long left=beginTime-current;
        String describe=currentActivity.getDescription();
        if(left>0){
            long hour=left/HourLen;
            long min=(left-hour*HourLen)/MinuteLen;
            long second=(left-hour*HourLen-min*MinuteLen)/SencondLen;
            StringBuilder sb=new StringBuilder();
            sb.append(describe).append(" ");
            sb.append(prefix);
            if(hour!=0){
                sb.append(hour).append("小时");
            }
            if(min!=0){
                sb.append(min).append("分");
            }
            sb.append(second).append("秒");
//            if(hour<10){
//                sb.append("0");
//            }
//            sb.append(hour).append(":");
//            if(min<10){
//                sb.append("0");
//            }
//            sb.append(min).append(":");
//            if(second<10){
//                sb.append("0");
//            }
//            sb.append(second);
            return sb.toString();
        }else{
            //时间到则直接进入视频页面并结束本页面
            return null;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webpage);
        Intent intent=getIntent();
        currentActivity=intent.getBundleExtra(ActivityBundle).getParcelable(ActivityKey);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        myWebView= (WebView) findViewById(R.id.webpage);
        //加载服务器上的页面
        myWebView.loadUrl(currentActivity.getImgUrl());
        //加载本地中的html
        //myWebView.loadUrl("file:///android_asset/www/test2.html");
        //加上下面这段代码可以使网页中的链接不以浏览器的方式打开
        myWebView.setWebViewClient(new WebViewClient());
        //得到webview设置
        WebSettings webSettings = myWebView.getSettings();
        //允许使用javascript
        webSettings.setJavaScriptEnabled(true);
        toMessage= (ImageView) findViewById(R.id.to_message);
        toMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserCenterActivity.startMe(context);
            }
        });
        textView= (TextView) findViewById(R.id.time);
        handler.postDelayed(changeTime,0);
    }

    public static void startMe(Context context,ActivityDetail detail) {
        if (accountInfo != null) {
            context.startActivity(ActivityDetail.createInentWithActivity(context,WebWealcomeActivity.class,detail));
        }
    }
}
