package com.sx.dw.accountAndSecurity.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.widget.EditText;

import com.apkfuns.logutils.LogUtils;
import com.sx.dw.R;
import com.sx.dw.accountAndSecurity.helper.AccountApi;
import com.sx.dw.accountAndSecurity.helper.ILoginHelper;
import com.sx.dw.accountAndSecurity.helper.LoginHelperImpl;
import com.sx.dw.core.BaseActivity;
import com.sx.dw.core.network.DwCallback;
import com.sx.dw.core.network.DwRetrofit;
import com.sx.dw.core.network.EntityHead;
import com.sx.dw.core.util.AES;
import com.sx.dw.core.util.C;
import com.sx.dw.core.util.InputAttempt;
import com.sx.dw.core.util.MD5;
import com.sx.dw.core.util.ToastUtil;
import com.sx.dw.wealth.TokenEntity;
import com.sx.dw.wealth.TokenUtil;
import com.sx.dw.wealth.WithdrawActivity;

import retrofit2.Call;

import static com.sx.dw.core.GlobalData.accountInfo;
import static com.sx.dw.core.GlobalData.setAccountInfo;
import static com.sx.dw.core.util.C.IntentExtra.ACCOUNT;
import static com.sx.dw.core.util.C.IntentExtra.REQUEST_CODE;
import static com.sx.dw.core.util.C.IntentExtra.TOKEN;
import static com.sx.dw.core.util.C.RequestCode.BIND_AND_SET_PAY_PSD;
import static com.sx.dw.core.util.C.RequestCode.FORGET_PASSWORD;
import static com.sx.dw.core.util.C.RequestCode.LOGIN_BIND_PHONE;
import static com.sx.dw.core.util.C.RequestCode.RESET_PASSWORD;
import static com.sx.dw.core.util.C.RequestCode.SET_PAY_PSD;
import static com.sx.dw.core.util.C.RequestCode.SET_PAY_PSD_AND_WITHDRAW;

public class SetPasswordActivity extends BaseActivity {


    private EditText etInputPassword;
    private EditText etInputPasswordAgain;

    private AccountApi accountApi;

    private int requestType;

    public static void startMe(BaseActivity activity, String account, String token, int requestCode) {
        Intent intent = new Intent(activity, SetPasswordActivity.class);
        intent.putExtra(ACCOUNT, account);
        intent.putExtra(TOKEN, token);
        intent.putExtra(REQUEST_CODE, requestCode);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        requestType = getIntent().getIntExtra(REQUEST_CODE, -99);

        etInputPassword = (EditText) findViewById(R.id.et_input_password);
        etInputPasswordAgain = (EditText) findViewById(R.id.et_input_password_again);
        initTitle();
        accountApi = DwRetrofit.getInstance().createApi(AccountApi.class);

    }

    private void initTitle() {
        switch (requestType) {
            case RESET_PASSWORD: {
                setTitle("重置密码");
                break;
            }
            case FORGET_PASSWORD: {
                setTitle("重置密码");
                break;
            }
            case BIND_AND_SET_PAY_PSD:
            case SET_PAY_PSD_AND_WITHDRAW: {
                setTitle("设置支付密码");
                initPayPsdEditText(etInputPassword);
                initPayPsdEditText(etInputPasswordAgain);
                break;
            }
            case SET_PAY_PSD: {
                setTitle("重置支付密码");
                initPayPsdEditText(etInputPassword);
                initPayPsdEditText(etInputPasswordAgain);
                break;
            }


        }
    }

    private void initPayPsdEditText(EditText inputEdit) {
        String digits = "0123456789";
        inputEdit.setKeyListener(DigitsKeyListener.getInstance(digits));
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(6);
        inputEdit.setFilters(filters);
    }

    private void setLoginPassword() {
        EditText[] editTexts = {etInputPasswordAgain, etInputPassword};
        InputAttempt.CheckRule[] rules = {InputAttempt.getPasswordAgainRule(etInputPassword), InputAttempt.getPasswordRule()};
        if (InputAttempt.attemptInput(editTexts, rules)) {
            showLoading();
            final String account = getIntent().getStringExtra(ACCOUNT);
            String token = getIntent().getStringExtra(TOKEN);
            final String passwordMd5 = MD5.GetMD5Code(etInputPassword.getText().toString());
            String password = AES.encode(passwordMd5);
            accountApi.resetUserPassWord(account, password, token).enqueue(new DwCallback<EntityHead>(this) {
                @Override
                public void getBody(Call call, EntityHead body) {
                    if (body.isSuccess()) {
                        dismissLoading();
                        accountInfo.setPassword(passwordMd5);
                        setAccountInfo(accountInfo);
                        if (requestType == LOGIN_BIND_PHONE) {
                            new LoginHelperImpl(SetPasswordActivity.this).loginHX(accountInfo, loginCallback);
                        } else {
                            ToastUtil.showToast("密码设置成功");
                            setResult(RESULT_OK);
                            finish();
                        }
                    } else {
                        dismissLoading();
                        ToastUtil.showToast(body.getMsg());
                    }
                }

                @Override
                public void onFailure(Call<EntityHead> call, Throwable t) {
                    super.onFailure(call, t);
                    if(requestType == LOGIN_BIND_PHONE){
                        startActivity(new Intent(SetPasswordActivity.this,LoginActivity.class));
                        ToastUtil.showToast("请重试");
                        finish();
                    }
                }
            });

        }
    }

    private void setPayPassword() {
        EditText[] editTexts = {etInputPasswordAgain, etInputPassword};
        InputAttempt.CheckRule[] rules = {InputAttempt.getPayPsdAgainRule(etInputPassword), InputAttempt.getPayPsdRule()};
        if (InputAttempt.attemptInput(editTexts, rules)) {
            showLoading();
            String token = getIntent().getStringExtra(TOKEN);
            final String passwordMd5 = MD5.GetMD5Code(etInputPassword.getText().toString());
            TokenEntity tokenEntity = TokenUtil.getInstance().getToken();
            String password = AES.encode(passwordMd5, tokenEntity.getKey());
            accountApi.setPayPsd(accountInfo.getId(), password, token).enqueue(new DwCallback<EntityHead>(this) {
                @Override
                public void getBody(Call call, EntityHead body) {
                    if (body.isSuccess()) {
                        dismissLoading();
                        if (requestType == SET_PAY_PSD_AND_WITHDRAW) {
                            ToastUtil.showToast("设置成功");
                            WithdrawActivity.startMe(SetPasswordActivity.this, C.RequestCode.JUST_BIND_WECHAT);
                        } else if (requestType == SET_PAY_PSD) {
                            ToastUtil.showToast("修改成功");
                        }
                        accountInfo.setCanWithdraw(true);
                        setAccountInfo(accountInfo);
                        finish();
                    } else {
                        dismissLoading();
                        ToastUtil.showToast(body.getMsg());
                    }
                }
            });

        }
    }

    public void setPassword(View view) {
        if (requestType == SET_PAY_PSD_AND_WITHDRAW || requestType == SET_PAY_PSD||requestType== BIND_AND_SET_PAY_PSD) {
            setPayPassword();
        } else {
            setLoginPassword();
        }
    }

    private ILoginHelper.LoginCallback loginCallback = new ILoginHelper.LoginCallback() {
        @Override
        public void onSuccess() {
            dismissLoading();
            LoadingActivity.startMe(SetPasswordActivity.this);
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

}
