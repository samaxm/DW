package com.sx.dw.accountAndSecurity.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.sx.dw.R;
import com.sx.dw.accountAndSecurity.helper.AccountApi;
import com.sx.dw.accountAndSecurity.helper.PhoneAboutApi;
import com.sx.dw.core.BaseActivity;
import com.sx.dw.core.network.DwCallback;
import com.sx.dw.core.network.DwRetrofit;
import com.sx.dw.core.network.EntityHead;
import com.sx.dw.core.util.InputAttempt;
import com.sx.dw.core.util.ToastUtil;

import retrofit2.Call;

import static com.sx.dw.core.GlobalData.accountInfo;
import static com.sx.dw.core.util.C.IntentExtra.REQUEST_CODE;
import static com.sx.dw.core.util.C.RequestCode.BIND_AND_SET_PAY_PSD;
import static com.sx.dw.core.util.C.RequestCode.BIND_PHONE;
import static com.sx.dw.core.util.C.RequestCode.FORGET_PASSWORD;
import static com.sx.dw.core.util.C.RequestCode.LOGIN_BIND_PHONE;
import static com.sx.dw.core.util.C.RequestCode.RESET_PASSWORD;
import static com.sx.dw.core.util.C.RequestCode.SET_PAY_PSD;
import static com.sx.dw.core.util.C.RequestCode.SET_PAY_PSD_AND_WITHDRAW;

/**
 * 验证手机模块，多处调用，通过判断requestCode来执行不同的接口和跳转操作
 */
public class CheckPhoneActivity extends BaseActivity {


    private TextInputLayout inputLayout;
    private EditText etInputPhoneNumber;
    private EditText etInputPhoneCode;
    private Button btnSendCode;

    private AccountApi accountApi;
    private PhoneAboutApi phoneAboutApi;

    private CountDownTimer countDownTimer;
    private int requestType;

    public static void startMe(BaseActivity activity, int requestCode) {
        Intent intent = new Intent(activity, CheckPhoneActivity.class);
        intent.putExtra(REQUEST_CODE, requestCode);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_phone);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        requestType = getIntent().getIntExtra(REQUEST_CODE, 0);

        inputLayout = (TextInputLayout) findViewById(R.id.inputLayout_input_phone_num);
        etInputPhoneNumber = (EditText) findViewById(R.id.et_input_phone_number);
        etInputPhoneCode = (EditText) findViewById(R.id.et_input_phone_code);
        btnSendCode = (Button) findViewById(R.id.btn_send_code);

        initTitle();

        accountApi = DwRetrofit.getInstance().createApi(AccountApi.class);
        phoneAboutApi = DwRetrofit.getInstance().createApi(PhoneAboutApi.class);
    }

    private void initTitle() {
        switch (requestType) {
            case FORGET_PASSWORD: {
                setTitle("找回密码");
                break;
            }
            case RESET_PASSWORD: {
                setTitle("重置密码");
                initPhoneInputEdit();
                break;
            }
            case BIND_PHONE:
            case  BIND_AND_SET_PAY_PSD:
            case LOGIN_BIND_PHONE:{
                setTitle("绑定手机");
                inputLayout.setHint("请输入要绑定的手机号码");
                break;
            }
            case SET_PAY_PSD_AND_WITHDRAW: {
                setTitle("设置支付密码");
                initPhoneInputEdit();
                break;
            }
            case SET_PAY_PSD:{
                setTitle("设置支付密码");
                initPhoneInputEdit();
                break;
            }

        }
    }

    private void initPhoneInputEdit() {
        if (!TextUtils.isEmpty(accountInfo.getPhone())) {
            etInputPhoneNumber.setText(accountInfo.getPhone());
            etInputPhoneNumber.setFocusable(false);
            etInputPhoneNumber.setEnabled(false);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case FORGET_PASSWORD: {
                    setResult(RESULT_OK);
                    finish();
                    break;
                }
                case BIND_PHONE: {
                    setResult(RESULT_OK);
                    finish();
                    break;
                }
                case RESET_PASSWORD:{
                    setResult(RESULT_OK);
                    finish();
                    break;
                }

            }
        }
    }

    private Call<EntityHead> getCheckPhoneCall(String phoneNumber, String phoneCode) {
        Call<EntityHead> call = null;
        if (requestType == RESET_PASSWORD || requestType == FORGET_PASSWORD) {
            call = phoneAboutApi.checkForgetPasswordPhoneCode(phoneNumber, phoneCode);
        } else if (requestType == BIND_PHONE || requestType == LOGIN_BIND_PHONE) {
            call = phoneAboutApi.bindPhoneNumber(accountInfo.getId(), phoneNumber, phoneCode,false);
        } else if (requestType == SET_PAY_PSD_AND_WITHDRAW || requestType == SET_PAY_PSD) {
            call = phoneAboutApi.checkSetPayPsdCode(accountInfo.getId(), phoneNumber, phoneCode);
        } else if(requestType==BIND_AND_SET_PAY_PSD){
            call = phoneAboutApi.bindPhoneNumber(accountInfo.getId(), phoneNumber, phoneCode,true);
        }
        return call;
    }
    private Call<EntityHead> getSendCodeCall(String phoneNum) {
        Call<EntityHead> call = null;
        if (requestType == RESET_PASSWORD || requestType == FORGET_PASSWORD) {
            call = phoneAboutApi.sendForgetPasswordPhoneCode(phoneNum);
        } else if (requestType == BIND_PHONE || requestType == LOGIN_BIND_PHONE|| requestType==BIND_AND_SET_PAY_PSD) {
            call = phoneAboutApi.sendRegisterPhoneCode(phoneNum);
        }else if (requestType == SET_PAY_PSD_AND_WITHDRAW|| requestType == SET_PAY_PSD) {
            call = phoneAboutApi.sendSetPayPsdCode(accountInfo.getId(),phoneNum);
        }
        return call;
    }

    //    验证手机按钮的点击事件
    public void checkPhone(View view) {
        EditText[] editTexts = {etInputPhoneNumber, etInputPhoneCode};
        InputAttempt.CheckRule[] rules = {InputAttempt.getPhoneRule(), InputAttempt.getCodeRule()};
//        输入正确
        if (InputAttempt.attemptInput(editTexts, rules)) {
            final String phoneNumber = etInputPhoneNumber.getText().toString();
            String phoneCode = etInputPhoneCode.getText().toString();
            getCheckPhoneCall(phoneNumber, phoneCode).enqueue(new DwCallback<EntityHead>() {
                @Override
                public void getBody(Call call, EntityHead body) {
                    if (body.isSuccess()) {
                        if (body.getData() == null) {
                            ToastUtil.showToast("数据返回异常");
                            return;
                        }
                        if(accountInfo!=null) {
                            accountInfo.setPhone(phoneNumber);
                        }
                        String token = body.getData().toString();
                        SetPasswordActivity.startMe(CheckPhoneActivity.this, phoneNumber, token, requestType);
                        ToastUtil.showToast("验证成功");
                        finish();
                    } else {
                        ToastUtil.showToast(body.getMsg());
                    }
                }
            });
        }
    }



    public void sendCode(View view) {
        if (InputAttempt.attemptInput(etInputPhoneNumber, InputAttempt.getPhoneRule())) {
            btnSendCode.setClickable(false);
            String phoneNum = etInputPhoneNumber.getText().toString();
            getSendCodeCall(phoneNum).enqueue(new DwCallback<EntityHead>() {
                @Override
                public void getBody(Call call, EntityHead body) {
                    if (body.isSuccess()) {
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
}
