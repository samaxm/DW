package com.sx.dw.accountAndSecurity.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.apkfuns.logutils.LogUtils;
import com.sx.dw.R;
import com.sx.dw.accountAndSecurity.entity.AccountInfoEntity;
import com.sx.dw.accountAndSecurity.entity.ActivityDetail;
import com.sx.dw.accountAndSecurity.entity.ActivityList;
import com.sx.dw.accountAndSecurity.entity.CheckVipCodeResultEntity;
import com.sx.dw.accountAndSecurity.helper.AccountApi;
import com.sx.dw.accountAndSecurity.helper.AppApi;
import com.sx.dw.accountAndSecurity.helper.ILoginHelper;
import com.sx.dw.accountAndSecurity.helper.IRegisterHelper;
import com.sx.dw.accountAndSecurity.helper.LoginHelperImpl;
import com.sx.dw.accountAndSecurity.helper.RegisterHelperImpl;
import com.sx.dw.core.BaseActivity;
import com.sx.dw.core.network.DwCallback;
import com.sx.dw.core.network.DwRetrofit;
import com.sx.dw.core.network.EntityHead;
import com.sx.dw.core.util.AES;
import com.sx.dw.core.util.C;
import com.sx.dw.core.util.InputAttempt;
import com.sx.dw.core.util.MD5;
import com.sx.dw.core.util.ToastUtil;
import com.sx.dw.util.DateHelper;
import com.sx.dw.version.UpdateHelper;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.sx.dw.core.GlobalData.accountInfo;
import static com.sx.dw.core.GlobalData.getLatestActivity;
import static com.sx.dw.core.GlobalData.updateActivityList;

/**
 * 启动页面，包含验证vip码，检查登录，检查更新
 */
public class LauncherActivity extends BaseActivity {

    private TextView[] tvCode;
    private android.widget.EditText etInputCode;
    private String vipCode;
    private ImageView tvNoCode;
    private AppApi appApi;
    private IRegisterHelper registerHelper;
    private ILoginHelper loginHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_launcher);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        tvCode = new TextView[4];
        tvCode[0] = (TextView) findViewById(R.id.tv_code1);
        tvCode[1] = (TextView) findViewById(R.id.tv_code2);
        tvCode[2] = (TextView) findViewById(R.id.tv_code3);
        tvCode[3] = (TextView) findViewById(R.id.tv_code4);
        etInputCode = (EditText) findViewById(R.id.et_input_code);
        etInputCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                char[] chars = s.toString().toCharArray();
                int length = tvCode.length;
                for (int i = 0; i < length; i++) {
                    if (i < chars.length) {
                        tvCode[i].setText(chars[i] + "");
                    } else {
                        tvCode[i].setText("");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        tvNoCode = (ImageView) findViewById(R.id.tv_no_code);
        initNoCode();
        updateActivity();
        registerHelper = new RegisterHelperImpl(this);
        loginHelper=new LoginHelperImpl(this);
    }

    private void initNoCode() {
        if(isCheckVip()){
            tvNoCode.setVisibility(View.INVISIBLE);
        }else {
            tvNoCode.setVisibility(View.VISIBLE);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        new UpdateHelper(this).checkUpdate(false);
        checkLoginV2();
    }

    public void onClick(View view) {
        onResume();
    }


    /**
     * 若已登录直接到过渡界面
     */
    private void checkLoginV2() {
        LoginHelperImpl helper = new LoginHelperImpl(this);
        if (helper.isHXLoggedIn() && accountInfo != null) {
            LoadingActivity.startMe(this);
            finish();
        }
    }

    private void checkLogin() {
        LoginHelperImpl helper = new LoginHelperImpl(this);
        if (helper.isHXLoggedIn() && accountInfo != null) {
            LoadingActivity.startMe(this);
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }
        finish();
    }


    public void onNoCodeClick(View view) {
        registerHelper.authRegister(SHARE_MEDIA.WEIXIN, authRegisterCallback);
    }

    public void onCheckCodeClick(View view) {
        vipCode = etInputCode.getText().toString();
        if (TextUtils.isEmpty(vipCode)) {
            ToastUtil.showToast("请输入邀请码哦");
            return;
        }
        String code = AES.encode(vipCode);
        DwRetrofit.getInstance().createApi(AccountApi.class)
                .checkVipCode(code)
                .enqueue(new DwCallback<EntityHead<CheckVipCodeResultEntity>>() {
                    @Override
                    public void getBody(Call call, final EntityHead<CheckVipCodeResultEntity> body) {
                        if (body.isSuccess()) {
                            String dwid = body.getData().getDwid();
                            saveVipState();
                            JPushInterface.setAlias(context, dwid, new TagAliasCallback() {
                                @Override
                                public void gotResult(int i, String s, Set<String> set) {
                                    checkVipCodeResult(body.getData());
                                }
                            });
                        } else {
                            ToastUtil.showToast(body.getMsg());
                        }
                    }
                });
    }

    private void saveVipState() {
        SharedPreferences sharedPreferences = getSharedPreferences(C.SP_FILE_NAME_VIP, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(C.SP_KEY_VIP, true);
        editor.commit();
        initNoCode();
    }

    private void updateActivity(){
        ActivityDetail detail=getLatestActivity();
        long currentDateNum = DateHelper.getCurrentDateNum();
        if(detail==null) {
            appApi = DwRetrofit.getInstance().createApi(AppApi.class);
            appApi.getTodayActivity(currentDateNum).enqueue(new Callback<EntityHead<ActivityList>>() {
                @Override
                public void onResponse(Call<EntityHead<ActivityList>> call, Response<EntityHead<ActivityList>> response) {
                    if (response.body() != null) {
                        ActivityList list = response.body().getData();
                        updateActivityList(list);
                    }
                }

                @Override
                public void onFailure(Call<EntityHead<ActivityList>> call, Throwable t) {
                }
            });
        }
    }

    private boolean isCheckVip(){
        SharedPreferences sharedPreferences = getSharedPreferences(C.SP_FILE_NAME_VIP, 0);
        return sharedPreferences.getBoolean(C.SP_KEY_VIP,false);
    }


    private void checkVipCodeResult(CheckVipCodeResultEntity data) {

        if (CheckVipCodeResultEntity.CHECKED.equals(data.getStatus())) {
            PayVipChargeActivity.startMe(this, data.getDwid(), vipCode, C.RequestCode.CHECK_VIP_CODE_TO_PAY);
        } else if (CheckVipCodeResultEntity.UNUSED.equals(data.getStatus())) {
            ToastUtil.showToast("邀请码无效");
        } else if (CheckVipCodeResultEntity.PAID.equals(data.getStatus())) {
            RegisterActivity.startMe(this, vipCode, C.RequestCode.VIP_REGISTER);
        } else if (CheckVipCodeResultEntity.REGISTER.equals(data.getStatus())) {
            ToastUtil.showToast("邀请码已经注册，请直接登录");
            startActivity(new Intent(this, LoginActivity.class));
        }
    }


    private IRegisterHelper.RegisterCallback authRegisterCallback = new IRegisterHelper.RegisterCallback() {
        @Override
        public void onSuccess(final AccountInfoEntity entity) {
            LogUtils.d(entity);
                loginHelper.loginHX(entity, partyLoginCallback);
                finish();
        }
        @Override
        public void onFailure(String msgToUser) {

        }
    };

    private ILoginHelper.LoginCallback partyLoginCallback = new ILoginHelper.LoginCallback() {
        @Override
        public void onSuccess() {
            LoadingActivity.startMe(LauncherActivity.this);
            finish();
            LogUtils.d("loginCallback.onSuccess");
        }

        @Override
        public void onError(String msg) {
            LogUtils.d(msg);
            ToastUtil.showToast(msg);
        }
    };
}
