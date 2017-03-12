package com.sx.dw.wealth;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sx.dw.R;
import com.sx.dw.accountAndSecurity.ui.CheckPhoneActivity;
import com.sx.dw.core.BaseActivity;
import com.sx.dw.core.util.C;
import com.sx.dw.core.util.ToastUtil;

import static com.sx.dw.core.GlobalData.accountInfo;
import static com.sx.dw.core.GlobalData.setAccountInfo;
import static com.sx.dw.core.util.C.RequestCode.BIND_AND_SET_PAY_PSD;
import static com.sx.dw.core.util.C.RequestCode.SET_PAY_PSD;
import static com.sx.dw.core.util.C.RequestCode.SET_PAY_PSD_AND_WITHDRAW;

public class WealthActivity extends BaseActivity {

    private TextView tvWealth;
    private LinearLayout llWorthLayout;
    private TextView tvWorth;
    private LinearLayout llWithdraw;
    private LinearLayout llRecharge;
    private LinearLayout llSetPayPsd;
    private TextView tvSetPayPsdHint;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wealth);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tvWealth = (TextView) findViewById(R.id.tv_wealth);
        llWorthLayout = (LinearLayout) findViewById(R.id.ll_worth_layout);
        tvWorth = (TextView) findViewById(R.id.tv_worth);
        llWithdraw = (LinearLayout) findViewById(R.id.ll_withdraw);
        llRecharge = (LinearLayout) findViewById(R.id.ll_recharge);
        llSetPayPsd = (LinearLayout) findViewById(R.id.ll_set_pay_psd);
        tvSetPayPsdHint = (TextView) findViewById(R.id.tv_set_pay_psd_hint);

        llWithdraw.setOnClickListener(onClickListener);
        llRecharge.setOnClickListener(onClickListener);
        llSetPayPsd.setOnClickListener(onClickListener);

        initViewData();
    }

    private void initViewData() {
        tvWealth.setText(accountInfo.getWealthString());
        llWorthLayout.setVisibility(View.VISIBLE);
        tvWorth.setText(accountInfo.getWorthString());
        llWorthLayout.setOnClickListener(onClickListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        WealthHelper.getInstance().getUserWealth(tvWealth);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(accountInfo.isUserCanWithdraw()){
            tvSetPayPsdHint.setText("修改支付密码");
        }else {
            tvSetPayPsdHint.setText("设置支付密码");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        setAccountInfo();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == C.RequestCode.SET_WORTH && resultCode == RESULT_OK){
            initViewData();
        }
    }



    private void checkWithdraw() {
        if(accountInfo.isUserCanWithdraw()){
            startActivity(new Intent(WealthActivity.this,WithdrawActivity.class));
        }else {
            explainWithdrawPremise();
        }
    }


    private void explainWithdrawPremise() {
        final boolean bindAccount = !TextUtils.isEmpty(accountInfo.getAccount());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if(bindAccount){
            builder.setMessage("兑现需要绑定手机并设置支付密码，现在设置吗？");
        }else{
            builder.setMessage("兑现需要绑定微信账户和设置支付密码，现在绑定吗？");
        }
        builder.setNegativeButton("等一下！", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!bindAccount){
                    bindWeChatAccount(SET_PAY_PSD_AND_WITHDRAW);
                }else if(TextUtils.isEmpty(accountInfo.getPhone())){
                    CheckPhoneActivity.startMe(WealthActivity.this,BIND_AND_SET_PAY_PSD);
                }else if(!accountInfo.isUserCanWithdraw()){
                    CheckPhoneActivity.startMe(WealthActivity.this, SET_PAY_PSD_AND_WITHDRAW);
                }
            }
        });
        builder.show();
    }

    private void bindWeChatAccount(final int requestCode) {
        WealthHelper.getInstance().bindWechat(this, new WealthHelper.BindWechatCallback() {
            @Override
            public void onSuccess() {
                CheckPhoneActivity.startMe(WealthActivity.this, requestCode);
                dismissLoading();
            }
        });
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.ll_recharge: {
                    startActivity(new Intent(WealthActivity.this, RechargeActivity.class));
                    break;
                }
                case R.id.ll_withdraw: {
                    checkWithdraw();
                    break;
                }
                case R.id.ll_set_pay_psd: {
                    if(!accountInfo.isUserCanWithdraw()){
                        ToastUtil.showToast("先绑定微信");
                        bindWeChatAccount(SET_PAY_PSD);
                    }else {
                        CheckPhoneActivity.startMe(WealthActivity.this, SET_PAY_PSD);
                    }
                    break;
                }
                case R.id.ll_worth_layout:{
                    SetWorthActivity.startMe(WealthActivity.this,C.RequestCode.SET_WORTH);
                    break;
                }
            }
        }
    };

}
