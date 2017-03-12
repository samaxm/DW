
package com.sx.dw.core;


import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.multidex.MultiDex;

import com.activeandroid.ActiveAndroid;
import com.apkfuns.logutils.LogLevel;
import com.avos.avoscloud.AVOSCloud;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.squareup.leakcanary.LeakCanary;
import com.sx.dw.video.model.CurrentUserSettings;
import com.sx.dw.video.model.WorkerThread;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.PlatformConfig;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.jpush.android.api.JPushInterface;
import okhttp3.OkHttpClient;

import static com.sx.dw.core.util.C.AVOS_CLOUD_CODE;
import static com.sx.dw.core.util.C.AVOS_CLOUD_KEY;
import static com.sx.dw.core.util.C.LOG_TAG;
import static com.sx.dw.core.util.C.QQ_APP_ID;
import static com.sx.dw.core.util.C.QQ_SIGN;
import static com.sx.dw.core.util.C.WECHAT_APP_ID;
import static com.sx.dw.core.util.C.WECHAT_SIGN;

public class DWApplication extends com.activeandroid.app.Application {
    private static DWApplication instance;
    private static List<BaseActivity> activityList;


    private WorkerThread mWorkerThread;

    public synchronized void initWorkerThread() {
        if (mWorkerThread == null) {
            mWorkerThread = new WorkerThread(getApplicationContext());
            mWorkerThread.start();

            mWorkerThread.waitForReady();
        }
    }

    public synchronized WorkerThread getWorkerThread() {
        return mWorkerThread;
    }

    public synchronized void deInitWorkerThread() {
        mWorkerThread.exit();
        try {
            mWorkerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mWorkerThread = null;
    }

    public static final CurrentUserSettings mVideoSettings = new CurrentUserSettings();


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
//        内存泄漏检测
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);

//       初始化环信
        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
        if (processAppName == null || !processAppName.equalsIgnoreCase(instance.getPackageName())) {
            return;
        }
        EMOptions options = new EMOptions();
        options.setAcceptInvitationAlways(false);
        options.setAutoLogin(true);
        EMClient.getInstance().init(instance, options);
        EMClient.getInstance().setDebugMode(AppConfig.IS_DEBUG);

//        初始化友盟第三方登录
        PlatformConfig.setWeixin(WECHAT_APP_ID, WECHAT_SIGN);
        PlatformConfig.setQQZone(QQ_APP_ID, QQ_SIGN);
//        初始化友盟
        MobclickAgent.setDebugMode(AppConfig.IS_DEBUG);
//        初始化极光
        JPushInterface.setDebugMode(AppConfig.IS_DEBUG);
        JPushInterface.init(this);
        ActiveAndroid.initialize(this);


//        初始化图片工具Fresco
        Fresco.initialize(this);
//        初始化LeanCloud
        AVOSCloud.initialize(this, AVOS_CLOUD_KEY, AVOS_CLOUD_CODE);

        //        初始化LogUtils
        com.apkfuns.logutils.LogUtils.getLogConfig()
                .configAllowLog(AppConfig.IS_DEBUG)//是否允许日志输出
                .configTagPrefix(LOG_TAG)
                .configShowBorders(false)//不显示边界
                .configLevel(LogLevel.TYPE_VERBOSE);
//        FIXME 调试数据库用的
        Stetho.initializeWithDefaults(this);
        new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();

    }


    public static void addActivity(BaseActivity activity) {
        if (activityList == null) {
            activityList = new ArrayList<>();
        }
        activityList.add(activity);
    }

    public static void delActivity(BaseActivity activity) {
        if (activityList != null && activityList.contains(activity)) {
            activityList.remove(activity);
        }
    }

    public static void exitApp() {
        for (BaseActivity activity : activityList) {
            activity.finish();
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static DWApplication getInstance() {
        return instance;
    }

    private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = this.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                // Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        return processName;
    }


}
