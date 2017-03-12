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
import com.sx.dw.core.BaseActivity;
import com.sx.dw.core.network.DwCallback;
import com.sx.dw.core.network.DwRetrofit;
import com.sx.dw.core.network.EntityHead;
import com.sx.dw.core.util.AES;
import com.sx.dw.core.util.InputAttempt;
import com.sx.dw.core.util.MD5;
import com.sx.dw.core.util.ToastUtil;

import retrofit2.Call;

import static com.sx.dw.core.GlobalData.accountInfo;
import static com.sx.dw.core.GlobalData.setAccountInfo;

public class SetWorthActivity extends BaseActivity {

    private EditText etInputWorth;
    private TextView tvPrice;
    private WealthApi wealthApi;
    private int worth;

    public static void startMe(Activity activity,int requestCode){
        Intent intent = new Intent(activity,SetWorthActivity.class);
        activity.startActivityForResult(intent,requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_worth);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        etInputWorth = (EditText) findViewById(R.id.et_input_worth);
        tvPrice = (TextView) findViewById(R.id.tv_price);
        etInputWorth.addTextChangedListener(textWatcher);

        wealthApi = DwRetrofit.getInstance().createApi(WealthApi.class);

    }


    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String countStr = charSequence.toString();
            if (!TextUtils.isEmpty(countStr)) {
                try {
                    worth = Integer.parseInt(countStr);
                } catch (NumberFormatException e) {
                    ToastUtil.showToast("请不要输入奇怪的东西");
                }
                if (worth > accountInfo.getWealth()) {
                    tvPrice.setText("身家不足哦");
                } else {
                    tvPrice.setText(worth + "");
                }
            } else {
                tvPrice.setText("0");
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };


    public void setWorthClick(View view) {
        if(!accountInfo.isUserCanWithdraw()){
            ToastUtil.showToast("您还未设置支付密码，请先设置支付密码哦");
            return;
        }
        if (worth > accountInfo.getWealth()) {
            ToastUtil.showToast("身家不足");
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
                            setWorth(AES.encode(MD5.GetMD5Code(psd), TokenUtil.getInstance().getKey()));
                        }
                    }
                }).show();
    }

    private void setWorth(String psd) {
        wealthApi.setWorth(accountInfo.getId(), psd, worth + "").enqueue(new DwCallback<EntityHead<Integer>>() {
            @Override
            public void getBody(Call call, EntityHead<Integer> body) {
                if(body.isSuccess()){
                    accountInfo.setWealth(body.getData());
                    accountInfo.setWorth(worth);
                    setAccountInfo();
                    setResult(RESULT_OK);
                    finish();
                }else {
                    ToastUtil.showToast(body.getMsg());
                }
            }
        });
    }
}
