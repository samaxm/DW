package com.sx.dw.accountAndSecurity.ui;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.apkfuns.logutils.LogUtils;
import com.sx.dw.R;
import com.sx.dw.accountAndSecurity.entity.AccountInfoEntity;
import com.sx.dw.accountAndSecurity.entity.ActivityDetail;
import com.sx.dw.accountAndSecurity.helper.AccountApi;
import com.sx.dw.core.BaseActivity;
import com.sx.dw.core.GlobalData;
import com.sx.dw.core.network.DwCallback;
import com.sx.dw.core.network.DwRetrofit;
import com.sx.dw.core.network.EntityHead;
import com.sx.dw.core.util.C;
import com.sx.dw.core.util.ToastUtil;
import com.sx.dw.im.entity.LinkMan;
import com.sx.dw.social.UserInfoApi;
import com.sx.dw.social.entity.LikeRecordEntity;
import com.sx.dw.social.entity.SetUserInfoRequestEntity;
import com.sx.dw.social.ui.WebWealcomeActivity;
import com.sx.dw.videoChat.VideoChatActivity;
import com.sx.dw.videoChat.WebGameActivity;

import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.sx.dw.core.GlobalData.accountInfo;
import static com.sx.dw.core.GlobalData.getLatestActivity;
import static com.sx.dw.core.GlobalData.setAccountInfo;
import static com.sx.dw.core.util.C.RequestCode.LOADING_TO_SETTING_WIFI;
import static com.sx.dw.core.util.C.RequestCode.LOADING_TO_VIDEO_CHAT;

/**
 * 过渡界面，权限提示，api>23的权限申请，初始化收藏联系人，输入标签等
 */
public class LoadingActivity extends BaseActivity {

    private static final int REQUEST_CODE_PER_VIDEO_CALL = 110;
    private EditText etInputSign;
    private AccountApi accountApi;
    private ActivityDetail currentActivity;

    public static void startMe(Context context) {
        if (accountInfo != null) {
            ActivityDetail detail=getLatestActivity();
            Long current= Calendar.getInstance().getTimeInMillis();
            if(detail==null||detail.getBegintime()<=current){
                context.startActivity(ActivityDetail.createInentWithActivity(context,LoadingActivity.class,detail));
            }else{
                //非活动时间进入网页欢迎页面
                context.startActivity(ActivityDetail.createInentWithActivity(context,WebWealcomeActivity.class,detail));
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        currentActivity=ActivityDetail.getActivityDetail(getIntent());
        etInputSign = (EditText) findViewById(R.id.et_input_sign);
        pullLinkMan();
        etInputSign.setText(accountInfo.getSign());
        accountApi=DwRetrofit.getInstance().createApi(AccountApi.class);
        Integer activityID=null;
        if(currentActivity!=null){
            activityID=currentActivity.getId();
        }
        accountApi.getUserTags(activityID).enqueue(new Callback<EntityHead<List<String>>>() {
            @Override
            public void onResponse(Call<EntityHead<List<String>>> call, Response<EntityHead<List<String>>> response) {
                if(response.body()!=null&&response.body().getData()!=null){
                    setTags(response.body().getData());
                }
            }
            @Override
            public void onFailure(Call<EntityHead<List<String>>> call, Throwable t) {
                LogUtils.w("TAG",t);
            }
        });

    }


    private void setTags(final List<String> text){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ViewGroup vg= (ViewGroup) findViewById(R.id.container);
                int amount=vg.getChildCount();
                for(int i=0;i<amount;i++){
                    View view=vg.getChildAt(i);
                    if(view instanceof TextView&&i<text.size()){
                        ((TextView)view).setText(text.get(i));
                    }
                }

            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PER_VIDEO_CALL) {
            highApiPermissionsCheck();
        }
    }

    private void pullLinkMan() {
        DwRetrofit.getInstance().createApi(UserInfoApi.class).getLikeList(accountInfo.getId())
                .enqueue(new DwCallback<EntityHead<List<LikeRecordEntity>>>() {
                    @Override
                    public void getBody(Call call, EntityHead<List<LikeRecordEntity>> body) {
                        if (body.isSuccess()) {
                            List<LikeRecordEntity> likes = body.getData();
                            for (LikeRecordEntity entity : likes) {
                                LinkMan linkMan = entity.getInfo();
                                linkMan.setLikeDate(entity.getTime());
                                if (linkMan == null) {
                                    break;
                                }
                                LogUtils.v(linkMan);
                                linkMan.setLike(true);
                                LinkMan model = GlobalData.linkManMap.get(linkMan.getDwID());
                                if (model != null) {
                                    model.update(linkMan);
                                } else {
                                    linkMan.save();
                                }
                            }
                            GlobalData.updateLinkMans();
                        } else {
                            ToastUtil.showToast(body.getMsg());
                        }
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
//        toVideoChat();
    }

    private void toVideoChat() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info != null && info.getType() == ConnectivityManager.TYPE_WIFI) {
//            WebGameActivity.startMe(this);
            VideoChatActivity.startMe(this, LOADING_TO_VIDEO_CHAT,currentActivity);
        } else {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setMessage("您现在没有连接wifi，视频聊天将会消耗大量流量，确定继续吗？")
                    .setPositiveButton("我土豪", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                            WebGameActivity.startMe(context);
                            VideoChatActivity.startMe(LoadingActivity.this, LOADING_TO_VIDEO_CHAT,currentActivity);
                        }
                    })
                    .setNegativeButton("去设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                            startActivityForResult(intent, C.RequestCode.LOADING_TO_SETTING_WIFI);
                        }
                    })
                    .create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
    }

    private void checkPer(){
        if (isFirstOn()) {
            permissionsHint();
        }else {
            toVideoChat();
        }

    }

    private void permissionsHint() {
        AlertDialog permissionsAlertDialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("权限说明");
        builder.setMessage("为了正常使用软件，健康交流，请务必同意相机和录音的授权^_^");
        builder.setPositiveButton("好的！", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(isApiBefore23()){
                    toVideoChat();
                }else {
                    highApiPermissionsCheck();
                }
            }
        });
        permissionsAlertDialog = builder.create();
        permissionsAlertDialog.setCanceledOnTouchOutside(false);

        permissionsAlertDialog.show();
    }

    private void highApiPermissionsCheck() {
        LogUtils.d("begin");
        final String[] pers = new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (isPerNotGot(pers[0]) || isPerNotGot(pers[1]) || isPerNotGot(pers[2]) || isPerNotGot(pers[3])) {
            LogUtils.d("if == true");
            boolean should = ActivityCompat.shouldShowRequestPermissionRationale(context, pers[0]) || ActivityCompat.shouldShowRequestPermissionRationale(context, pers[1]) || ActivityCompat.shouldShowRequestPermissionRationale(context, pers[2]) || ActivityCompat.shouldShowRequestPermissionRationale(context, pers[3]);

            if (should) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("权限说明");
                builder.setMessage("相机和录音是我们应用需要的核心权限，我们不会做坏事,请您务必同意授权！");
                builder.setPositiveButton("好，我相信你！", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(context, pers, REQUEST_CODE_PER_VIDEO_CALL);
                    }
                });
                builder.setNegativeButton("我就不", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);
                if (!context.isFinishing()) {
                    dialog.show();
                } else {
                    LogUtils.e("context finished...");
                }

            } else {
                ActivityCompat.requestPermissions(context, pers, REQUEST_CODE_PER_VIDEO_CALL);
            }

        }
    }

    private boolean isApiBefore23() {
        int version = 0;
        try {
            version = Integer.valueOf(android.os.Build.VERSION.SDK);
        } catch (NumberFormatException e) {
            LogUtils.e(e.getMessage());
        }
        return version < 23;
    }

    private boolean isFirstOn() {
        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        int currentVersion = info.versionCode;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int lastVersion = prefs.getInt("version", 0);
        LogUtils.d("lastVersion = " + lastVersion);
        if (currentVersion > lastVersion) {
            //如果当前版本大于上次版本，该版本属于第一次启动
            //将当前版本写入preference中，则下次启动的时候，据此判断，不再为首次启动
            prefs.edit().putInt("version", currentVersion).commit();
            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOADING_TO_SETTING_WIFI) {
            checkPer();
        } else if (requestCode == LOADING_TO_VIDEO_CHAT) {
            finish();
        }
    }

    public void onSignSelect(View view) {
        TextView tvSign = (TextView) view;
        etInputSign.setText(tvSign.getText());
    }

    public void inputOk(View view) {
        String sign = etInputSign.getText().toString();
        if(!TextUtils.isEmpty(sign)){
            accountInfo.setSign(sign);
            setAccountInfo();
            pushUserInfo();
            checkPer();
        }else {
            ToastUtil.showToast("输入不能为空");
        }
    }

    private void pushUserInfo() {
        SetUserInfoRequestEntity entity = new SetUserInfoRequestEntity();
        entity.setSign(accountInfo.getSign());
        String dwId = accountInfo.getId();
        String password = accountInfo.getPasswordAES();
        String info = entity.toJsonString();
        DwRetrofit.getInstance().createApi(UserInfoApi.class).setUserInfo(password, dwId, info)
                .enqueue(new DwCallback<EntityHead<AccountInfoEntity>>() {
                    @Override
                    public void getBody(Call call, EntityHead<AccountInfoEntity> body) {

                    }
                });
    }

    private boolean isPerNotGot(String perName) {
        boolean b = PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(context, perName);
        LogUtils.v("perName = " + perName + "b = " + b);
        return b;
    }
}
