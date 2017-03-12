package com.sx.dw.accountAndSecurity.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.apkfuns.logutils.LogUtils;
import com.avos.avoscloud.feedback.FeedbackAgent;
import com.sx.dw.R;
import com.sx.dw.accountAndSecurity.entity.AccountInfoEntity;
import com.sx.dw.accountAndSecurity.helper.AccountApi;
import com.sx.dw.accountAndSecurity.helper.ILoginHelper;
import com.sx.dw.accountAndSecurity.helper.IRegisterHelper;
import com.sx.dw.accountAndSecurity.helper.LoginHelperImpl;
import com.sx.dw.accountAndSecurity.helper.RegisterHelperImpl;
import com.sx.dw.core.BaseActivity;
import com.sx.dw.core.network.DwCallback;
import com.sx.dw.core.network.DwRetrofit;
import com.sx.dw.core.network.EntityHead;
import com.sx.dw.core.util.AES;
import com.sx.dw.core.util.InputAttempt;
import com.sx.dw.core.util.MD5;
import com.sx.dw.core.util.ToastUtil;
import com.ta.utdid2.android.utils.StringUtils;
import com.umeng.socialize.bean.SHARE_MEDIA;

import retrofit2.Call;

import static com.sx.dw.core.GlobalData.accountInfo;
import static com.sx.dw.core.util.C.RequestCode.FORGET_PASSWORD;

/**
 * 登录页面，包含快捷登录（注册）
 */
public class LoginActivity extends BaseActivity {

    private EditText etInputPhoneNumber;
    private EditText etInputPassword;
    private TextView tvForgetPassword;
    private Button btnLogin;
    private ImageView ivWechatLogin;
//    private ImageView ivQQLogin;

    private IRegisterHelper registerHelper;
    private ILoginHelper loginHelper;

    private AccountApi accountApi;

//    private TextView registerText;

    public static void startMe(Context context){
        if(context==null){
            return;
        }
        Intent intent=new Intent(context,LoginActivity.class);
        context.startActivity(intent);
    }

    private TextWatcher keyListener=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(btnLogin==null){
            }
            else{
                if(!StringUtils.isEmpty(etInputPhoneNumber.getText().toString())&&!StringUtils.isEmpty(etInputPassword.getText().toString())){
                    btnLogin.setTextColor(ContextCompat.getColor(context, R.color.black));
                    btnLogin.setEnabled(true);
                }else{
                    btnLogin.setTextColor(ContextCompat.getColor(context, R.color.disableBtnText));
                    btnLogin.setEnabled(false);
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setDisplayUseLogoEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        etInputPhoneNumber = (EditText) findViewById(R.id.et_input_phone_number);
        etInputPassword = (EditText) findViewById(R.id.et_input_password);

        etInputPhoneNumber.addTextChangedListener(keyListener);
        etInputPassword.addTextChangedListener(keyListener);


        tvForgetPassword = (TextView) findViewById(R.id.tv_forget_password);
        btnLogin = (Button) findViewById(R.id.btn_login);
        ivWechatLogin = (ImageView) findViewById(R.id.iv_login_wechat);
//        ivQQLogin = (ImageView) findViewById(R.id.iv_login_qq);
//        registerText= (TextView) findViewById(R.id.register);


//        响应键盘的确定键
        etInputPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    accountLogin();
                    return true;
                }
                return false;
            }
        });


//        registerText.setOnClickListener(onClickListener);
        tvForgetPassword.setOnClickListener(onClickListener);
        btnLogin.setOnClickListener(onClickListener);
        ivWechatLogin.setOnClickListener(onAuthClickListener);
//        ivQQLogin.setOnClickListener(onAuthClickListener);

        registerHelper = new RegisterHelperImpl(this);
        loginHelper = new LoginHelperImpl(this);

        accountApi = DwRetrofit.getInstance().createApi(AccountApi.class);


    }

    @Override
    protected void onResume() {
        super.onResume();
//        new UpdateHelper(this).checkUpdate(false);
//        checkLoginV2();
//        getCallManager();

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
    @Override
    protected void onStart() {
        super.onStart();
        dismissLoading();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        registerHelper.onActivityResult(requestCode, resultCode, data);
    }

    private void accountLogin() {
        EditText[] editTexts = {etInputPassword, etInputPhoneNumber};
        InputAttempt.CheckRule[] rules = {InputAttempt.getPasswordRule(), InputAttempt.getPhoneRule()};
        if (InputAttempt.attemptInput(editTexts, rules)) {
            showLoading("正在登录");
            String account = etInputPhoneNumber.getText().toString();
            final String passwordMd5 = MD5.GetMD5Code(etInputPassword.getText().toString());
            String password = AES.encode(passwordMd5);
            accountApi.accountLogin(account, password, "PHONENUM").enqueue(new DwCallback<EntityHead<AccountInfoEntity>>(this) {
                @Override
                public void getBody(Call call, EntityHead<AccountInfoEntity> body) {
                    if (body.isSuccess()) {
                        AccountInfoEntity entity = body.getData();
                        entity.setPassword(passwordMd5);
                        entity.setLoginType(AccountInfoEntity.LoginType.ACCOUNT);
                        loginHelper.loginHX(entity, loginCallback);
                    } else {
                        ToastUtil.showToast(body.getMsg());
                        dismissLoading();
                    }
                }
            });
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_login, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.action_register) {
//            startActivity(new Intent(this, RegisterActivity.class));
//        }
//        return super.onOptionsItemSelected(item);
//    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.btn_login) {
                accountLogin();
            }else  if (view.getId() == R.id.tv_forget_password) {
                CheckPhoneActivity.startMe(LoginActivity.this, FORGET_PASSWORD);
            }
//            else if(view.getId()==R.id.register){
//                startActivity(new Intent(context, RegisterActivity.class));
//            }
        }
    };

    private View.OnClickListener onAuthClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            SHARE_MEDIA platform = null;
            int id = view.getId();
            if (id == R.id.iv_login_wechat) {
                platform = SHARE_MEDIA.WEIXIN;
            }
//            if (id == R.id.iv_login_qq) {
//                platform = SHARE_MEDIA.QQ;
//            }
            if (platform != null && registerHelper != null) {
                registerHelper.authRegister(platform, authRegisterCallback);
            }
        }
    };

    private IRegisterHelper.RegisterCallback authRegisterCallback = new IRegisterHelper.RegisterCallback() {
        @Override
        public void onSuccess(final AccountInfoEntity entity) {
            LogUtils.d(entity);
//            是否设置过密码，有则验证
            if (entity.isInit()) {
                View root = LayoutInflater.from(LoginActivity.this).inflate(R.layout.dialog_input_password, null);
                final EditText editText = (EditText) root.findViewById(R.id.et_input_password);
                new AlertDialog.Builder(LoginActivity.this)
                        .setView(root)
                        .setTitle("验证密码")
                        .setPositiveButton("验证", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                boolean attemptInput = InputAttempt.attemptInput(editText, InputAttempt.getPasswordRule());
                                if (attemptInput) {
                                    entity.setPassword(MD5.GetMD5Code(editText.getText().toString()));
                                    loginHelper.loginHX(entity, loginCallback);
                                }
                            }
                        }).show();
//                手机是否为空？为空则强制绑定手机
//            } else if(TextUtils.isEmpty(entity.getPhone())){
//                CheckPhoneActivity.startMe(LoginActivity.this,LOGIN_BIND_PHONE);
//                finish();
////                绑定过手机没设置密码的老办法登录
            }
           else {
                loginHelper.loginHX(entity, partyLoginCallback);
                finish();
            }
        }

        @Override
        public void onFailure(String msgToUser) {
            LogUtils.d(msgToUser);
            dismissLoading();
        }
    };


    private ILoginHelper.LoginCallback partyLoginCallback = new ILoginHelper.LoginCallback() {
        @Override
        public void onSuccess() {
            LoadingActivity.startMe(LoginActivity.this);
            finish();
            LogUtils.d("loginCallback.onSuccess");
        }

        @Override
        public void onError(String msg) {
            LogUtils.d(msg);
            ToastUtil.showToast(msg);
        }
    };

    @Override
    protected void onDestroy() {
        dismissLoading();
        super.onDestroy();
    }

    private ILoginHelper.LoginCallback loginCallback = new ILoginHelper.LoginCallback() {
        @Override
        public void onSuccess() {
            dismissLoading();
            LoadingActivity.startMe(LoginActivity.this);
            finish();
            LogUtils.d("loginCallback.onSuccess");
        }

        @Override
        public void onError(String msg) {
            dismissLoading();
            LogUtils.d(msg);
            ToastUtil.showToast(msg);
        }
    };


    public void loginHelp(View view) {
        FeedbackAgent agent = new FeedbackAgent(context);
        agent.startDefaultThreadActivity();
    }
}
