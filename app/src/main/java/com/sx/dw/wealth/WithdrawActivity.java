package com.sx.dw.wealth;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.sx.dw.R;
import com.sx.dw.core.AppConfig;
import com.sx.dw.core.BaseActivity;
import com.sx.dw.core.network.DwCallback;
import com.sx.dw.core.network.DwRetrofit;
import com.sx.dw.core.network.EntityHead;
import com.sx.dw.core.util.AES;
import com.sx.dw.core.util.C;
import com.sx.dw.core.util.InputAttempt;
import com.sx.dw.core.util.MD5;
import com.sx.dw.core.util.ToastUtil;

import retrofit2.Call;

import static com.sx.dw.core.GlobalData.accountInfo;

public class WithdrawActivity extends BaseActivity {

    private TextView tvWealth;
    private EditText etInputWithdrawCount;
    private TextView tvPrice;
    private int amount;
    private boolean amountRight;


    public static void startMe(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, WithdrawActivity.class);
        intent.putExtra(C.IntentExtra.REQUEST_CODE, requestCode);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvWealth = (TextView) findViewById(R.id.tv_wealth);
        etInputWithdrawCount = (EditText) findViewById(R.id.et_input_withdraw_count);
        tvPrice = (TextView) findViewById(R.id.tv_price);
        etInputWithdrawCount.addTextChangedListener(textWatcher);
    }

    @Override
    protected void onStart() {
        super.onStart();
        WealthHelper.getInstance().getUserWealth(tvWealth);
    }

    public void withdrawClick(View view) {
        if(!amountRight){
            return;
        }
        View root = LayoutInflater.from(this).inflate(R.layout.dialog_input_pay_psd, null);
        final EditText editText = (EditText) root.findViewById(R.id.et_input_pay_psd);
        new AlertDialog.Builder(this)
                .setView(root)
                .setTitle("验证支付密码")
                .setPositiveButton("验证", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        boolean attemptInput = InputAttempt.attemptInput(editText, InputAttempt.getPayPsdRule());
                        if (attemptInput) {
                            String psd = editText.getText().toString();
                            withdraw(AES.encode(MD5.GetMD5Code(psd),TokenUtil.getInstance().getKey()));
                        }
                    }
                }).show();
    }

    private void withdraw(final String payPsd) {
        DwRetrofit.getInstance().createApi(WealthApi.class).withdraw(accountInfo.getId(),payPsd,amount+"")
                .enqueue(new DwCallback<EntityHead>() {
                    @Override
                    public void getBody(Call call, EntityHead body) {
                        if(body.isSuccess()){
                            ToastUtil.showToast("兑现成功！");
                            WealthHelper.getInstance().getUserWealth(tvWealth);
                        }else if(body.getStatusCode() == WealthApi.KEY_ERROR) {
                            TokenUtil.getInstance().updateToken(new TokenUtil.UpdateTokenCallback() {
                                @Override
                                public void onSuccess() {
                                    withdraw(payPsd);
                                }
                            });
                        }else {
                            ToastUtil.showToast(body.getMsg());
                        }
                    }
                });
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int c) {
            amountRight = false;
            String countStr = s.toString();
            if (!TextUtils.isEmpty(countStr)) {
                double count = 0;
                try {
                    count = Integer.parseInt(countStr);
                } catch (NumberFormatException e) {
                    ToastUtil.showToast("请不要输入奇怪的东西");
                }
                amount = (int) count;
                int min = AppConfig.IS_DEBUG?100:500;
                if(count < min){
                    tvPrice.setText("兑现数量不能少于"+min);
                }else if (count > accountInfo.getWealth()) {
                    tvPrice.setText("身家不足");
                } else {
                    tvPrice.setText("￥" + String.format("%.2f", count * 0.009));
                    amountRight = true;
                }
            } else {
                tvPrice.setText("￥0.00");
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


}
