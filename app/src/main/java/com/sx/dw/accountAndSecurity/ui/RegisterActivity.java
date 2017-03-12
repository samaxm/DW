package com.sx.dw.accountAndSecurity.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.apkfuns.logutils.LogUtils;
import com.avos.avoscloud.feedback.FeedbackAgent;
import com.sx.dw.R;
import com.sx.dw.accountAndSecurity.entity.AccountInfoEntity;
import com.sx.dw.accountAndSecurity.entity.PhoneRegisterRequestEntity;
import com.sx.dw.accountAndSecurity.entity.VipRegisterRequestEntity;
import com.sx.dw.accountAndSecurity.helper.AccountApi;
import com.sx.dw.accountAndSecurity.helper.ILoginHelper;
import com.sx.dw.accountAndSecurity.helper.IRegisterHelper;
import com.sx.dw.accountAndSecurity.helper.LoginHelperImpl;
import com.sx.dw.accountAndSecurity.helper.PhoneAboutApi;
import com.sx.dw.accountAndSecurity.helper.RegisterHelperImpl;
import com.sx.dw.core.BaseActivity;
import com.sx.dw.core.network.DwCallback;
import com.sx.dw.core.network.DwRetrofit;
import com.sx.dw.core.network.EntityHead;
import com.sx.dw.core.util.C;
import com.sx.dw.core.util.InputAttempt;
import com.sx.dw.core.util.MD5;
import com.sx.dw.core.util.ToastUtil;
import com.sx.dw.social.ui.SetUserInfoActivity;
import com.ta.utdid2.android.utils.StringUtils;
import com.umeng.socialize.bean.SHARE_MEDIA;

import retrofit2.Call;

import static com.sx.dw.core.GlobalData.setAccountInfo;
import static com.sx.dw.core.util.C.IntentExtra.REQUEST_CODE;
import static com.sx.dw.core.util.C.RequestCode.SETTING_INFO;

public class RegisterActivity extends BaseActivity {


    private EditText etInputPhoneNumber;
    private EditText etInputPassword;
    private EditText etInputPhoneCode;
    private Button btnSendCode;
    private Button registerBtn;
    private ImageView ivWechat;
    private ImageView ivQQ;
    private TextView toLogin;
    private ILoginHelper loginHelper;
    private IRegisterHelper registerHelper;

    private PhoneAboutApi phoneAboutApi;
    private AccountApi accountApi;

    private CountDownTimer countDownTimer;
    private int requestType;

    public static void startMe(BaseActivity activity,String vipCode, int requestCode) {
        Intent intent = new Intent(activity, RegisterActivity.class);
        intent.putExtra(REQUEST_CODE, requestCode);
        intent.putExtra(C.IntentExtra.VIP_CODE,vipCode);
        activity.startActivityForResult(intent, requestCode);
    }

    private TextWatcher changeEnable =new TextWatcher(){
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(registerBtn==null){
            }else{
                if(!StringUtils.isEmpty(etInputPhoneNumber.getText().toString())&&!StringUtils.isEmpty(etInputPassword.getText().toString())&&
                        !StringUtils.isEmpty(etInputPhoneCode.getText().toString())){
                    registerBtn.setTextColor(ContextCompat.getColor(context, R.color.black));
                    registerBtn.setEnabled(true);
                }else{
                    registerBtn.setTextColor(ContextCompat.getColor(context, R.color.disableBtnText));
                    registerBtn.setEnabled(false);
                }
            }
        }

    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        requestType = getIntent().getIntExtra(REQUEST_CODE, 0);

        etInputPhoneNumber = (EditText) findViewById(R.id.et_input_phone_number);
        etInputPassword = (EditText) findViewById(R.id.et_input_password);
        etInputPhoneCode = (EditText) findViewById(R.id.et_input_phone_code);

        btnSendCode = (Button) findViewById(R.id.btn_send_code);
        ivWechat = (ImageView) findViewById(R.id.iv_register_wechat);
        ivQQ = (ImageView) findViewById(R.id.iv_register_qq);
        toLogin= (TextView) findViewById(R.id.toLogin);

        toLogin.setOnClickListener(onClickListener);
        btnSendCode.setOnClickListener(onClickListener);
        ivWechat.setOnClickListener(onAuthClickListener);
        ivQQ.setOnClickListener(onAuthClickListener);

        registerBtn= (Button) findViewById(R.id.btn_register);
        registerBtn.setOnClickListener(onClickListener);

        loginHelper = new LoginHelperImpl(this);

        accountApi = DwRetrofit.getInstance().createApi(AccountApi.class);
        phoneAboutApi = DwRetrofit.getInstance().createApi(PhoneAboutApi.class);

        registerHelper = new RegisterHelperImpl(this);

        etInputPhoneCode.addTextChangedListener(changeEnable);
        etInputPassword.addTextChangedListener(changeEnable);
        etInputPhoneNumber.addTextChangedListener(changeEnable);


    }



    private void attemptRegister() {


        EditText[] editTexts = {etInputPassword, etInputPhoneCode, etInputPhoneNumber};
        InputAttempt.CheckRule[] rules = {InputAttempt.getPasswordRule(), InputAttempt.getCodeRule(), InputAttempt.getPhoneRule()};

        if (InputAttempt.attemptInput(editTexts, rules)) {

            String phoneNumber = etInputPhoneNumber.getText().toString();
            String password = etInputPassword.getText().toString();
            String phoneCode = etInputPhoneCode.getText().toString();

            PhoneRegisterRequestEntity entity = new PhoneRegisterRequestEntity();
            entity.setPhoneNum(phoneNumber);
            entity.setPassword(password);
            entity.setCode(phoneCode);
            registerByPhone(entity);

        }
    }

    public void registerByPhone(final PhoneRegisterRequestEntity entity) {
        showLoading("正在注册");
        String info;
        String type;
        if(requestType == C.RequestCode.VIP_REGISTER){
            VipRegisterRequestEntity vipEntity = new VipRegisterRequestEntity();
            vipEntity.setCode(getIntent().getStringExtra(C.IntentExtra.VIP_CODE));
            vipEntity.setFormat(VipRegisterRequestEntity.PHONECODE);
            vipEntity.setRegisterInfo(entity.toJsonString());
            info = vipEntity.toJsonString();
            type = "VIP";
        }else {
            info = entity.toJsonString();
            type = "PHONECODE";
        }
        LogUtils.d(entity);
        accountApi.registerV2(info, type).enqueue(new DwCallback<EntityHead<AccountInfoEntity>>(this) {
            @Override
            public void getBody(Call call, EntityHead<AccountInfoEntity> body) {
                if (body.isSuccess()) {
                    final AccountInfoEntity accountInfoEntity = body.getData();
                    accountInfoEntity.setPassword(entity.getPassword());
                    loginHelper.loginHX(accountInfoEntity, new ILoginHelper.LoginCallback() {
                        @Override
                        public void onSuccess() {
                            dismissLoading();
                            ToastUtil.showToast("登录成功");
                            setAccountInfo(accountInfoEntity);
                            SetUserInfoActivity.startMe(RegisterActivity.this, SETTING_INFO);
                            finish();
                        }

                        @Override
                        public void onError(String msg) {
                            dismissLoading();
                            ToastUtil.showToast(msg);
                            LogUtils.d(msg);
                        }
                    });
                    LogUtils.d(accountInfoEntity);
                } else {
                    ToastUtil.showToast(body.getMsg());
                    dismissLoading();
                }
            }
        });
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_register, menu);
//        return super.onCreateOptionsMenu(menu);
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.action_back_login) {
//            finish();
//        }
//        return super.onOptionsItemSelected(item);
//    }
//    }


    private void sendCode() {
        if (InputAttempt.attemptInput(etInputPhoneNumber, InputAttempt.getPhoneRule())) {
            String phoneNumber = etInputPhoneNumber.getText().toString();
            btnSendCode.setClickable(false);
            phoneAboutApi.sendRegisterPhoneCode(phoneNumber).enqueue(new DwCallback<EntityHead>() {
                @Override
                public void getBody(Call call, EntityHead body) {
                    if (body.isSuccess()) {
                        ToastUtil.showToast("验证码已发送，请注意查收");
                        downTime(60);
                    } else {
                        ToastUtil.showToast(body.getMsg());
                        btnSendCode.setClickable(true);
                    }
                }

                @Override
                public void onFailure(Call<EntityHead> call, Throwable t) {
                    super.onFailure(call, t);
                    btnSendCode.setClickable(true);
                }
            });

        }
    }

    private void downTime(final int seconds) {
        if (countDownTimer == null) {
            countDownTimer = new CountDownTimer(seconds * 1000, 1000) {
                @Override
                public void onTick(long l) {
                    btnSendCode.setText((l / 1000) + "后可重新发送");
                }

                @Override
                public void onFinish() {
                    btnSendCode.setText("重新发送");
                    btnSendCode.setClickable(true);
                }
            };
        }
        countDownTimer.start();
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_register:
                    attemptRegister();
                    break;
                case R.id.btn_send_code:
                    sendCode();
                    break;
                case R.id.toLogin:
                    finish();
                    break;
            }
        }
    };

    private View.OnClickListener onAuthClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            SHARE_MEDIA platform = null;
            int id = view.getId();
            if (id == R.id.iv_register_wechat) {
                platform = SHARE_MEDIA.WEIXIN;
            }
            if (id == R.id.iv_register_qq) {
                platform = SHARE_MEDIA.QQ;
            }
            if (platform != null && registerHelper != null) {
//                registerHelper.authRegister(platform,getIntent().getStringExtra(C.IntentExtra.VIP_CODE), authRegisterCallback);
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
                View root = LayoutInflater.from(RegisterActivity.this).inflate(R.layout.dialog_input_password, null);
                final EditText editText = (EditText) root.findViewById(R.id.et_input_password);
                new AlertDialog.Builder(RegisterActivity.this)
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
//                手机是否为空？为空则强制绑定手机（设置过密码的一定是绑定过手机的，姑且相信后台）
//            } else if(TextUtils.isEmpty(entity.getPhone())){
//                CheckPhoneActivity.startMe(RegisterActivity.this,LOGIN_BIND_PHONE);
//                finish();
////                绑定过手机没设置密码的老办法登录
            }
            else {
                loginHelper.loginHX(entity, callback);
                finish();
            }
        }

        @Override
        public void onFailure(String msgToUser) {
            LogUtils.d(msgToUser);
            dismissLoading();
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
//            LoadingActivity.startMe(RegisterActivity.this);
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

    private ILoginHelper.LoginCallback callback = new ILoginHelper.LoginCallback() {
        @Override
        public void onSuccess() {
//            LoadingActivity.startMe(RegisterActivity.this);
            finish();
            LogUtils.d("loginCallback.onSuccess");
        }

        @Override
        public void onError(String msg) {
            LogUtils.d(msg);
            ToastUtil.showToast(msg);
        }
    };


    public void loginHelp(View view) {
        FeedbackAgent agent = new FeedbackAgent(context);
        agent.startDefaultThreadActivity();
    }


}
