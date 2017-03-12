package com.sx.dw.videoChat;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.sx.dw.R;
import com.sx.dw.accountAndSecurity.entity.ActivityDetail;
import com.sx.dw.core.BaseActivity;
import com.sx.dw.social.ui.UserCenterActivity;

import static com.sx.dw.accountAndSecurity.entity.ActivityDetail.ActivityBundle;
import static com.sx.dw.accountAndSecurity.entity.ActivityDetail.ActivityKey;
import static com.sx.dw.core.GlobalData.accountInfo;

/**
 * Created by admin on 2017/3/7.
 */
public class WebGameActivity extends BaseActivity {
    private WebView myWebView ;


    public static void startMe(Context context) {
        Intent intent=new Intent(context,WebGameActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webgame);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        myWebView= (WebView) findViewById(R.id.webpage);
        //加载服务器上的页面
        myWebView.loadUrl("http://flash1.7k7k.com/h5/2016/rzpksl/index.html");
        //加载本地中的html
        //myWebView.loadUrl("file:///android_asset/www/test2.html");
        //加上下面这段代码可以使网页中的链接不以浏览器的方式打开
        myWebView.setWebViewClient(new WebViewClient());
        //得到webview设置
        WebSettings webSettings = myWebView.getSettings();
        //允许使用javascript
        webSettings.setJavaScriptEnabled(true);
    }
}
