package com.sx.dw.wealth;

import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.apkfuns.logutils.LogUtils;
import com.sx.dw.R;
import com.sx.dw.core.AppConfig;
import com.sx.dw.core.BaseActivity;
import com.sx.dw.core.network.DwCallback;
import com.sx.dw.core.network.DwRetrofit;
import com.sx.dw.core.network.EntityHead;
import com.sx.dw.core.util.C;
import com.sx.dw.core.util.ToastUtil;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.util.Map;

import retrofit2.Call;

import static com.sx.dw.core.GlobalData.accountInfo;

public class RechargeActivity extends BaseActivity {

    private EditText etInputRechargeCount;
    private TextView tvPrice;
    private ImageView ivWechatPay;
    private ImageView ivAliPay;

    private RechargeReceiver receiver;
    private IntentFilter filter;

    private WealthApi wealthApi;
    private int amount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ivAliPay = (ImageView) findViewById(R.id.iv_ali_pay);
        ivWechatPay = (ImageView) findViewById(R.id.iv_wechat_pay);
        tvPrice = (TextView) findViewById(R.id.tv_price);
        etInputRechargeCount = (EditText) findViewById(R.id.et_input_recharge_count);

        ivWechatPay.setOnClickListener(onPayBtnClickListener);
        ivAliPay.setOnClickListener(onPayBtnClickListener);
        etInputRechargeCount.addTextChangedListener(textWatcher);

        wealthApi = DwRetrofit.getInstance().createApi(WealthApi.class);

        if (receiver == null || filter == null) {
            filter = new IntentFilter();
            filter.addAction(C.BROADCAST_ACTION_RECHARGE);
            receiver = new RechargeReceiver(new RechargeReceiver.PayCallback() {
                @Override
                public void paySuccess() {
                    dismissLoading();
                    ToastUtil.showToast("充值成功");
                    WealthHelper.getInstance().getUserWealth();
                    finish();
                }
            });
        }
        registerReceiver(receiver, filter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private void wechatPay() {
        wealthApi.wxPayRecharge(accountInfo.getId(), 1, amount)
                .enqueue(new DwCallback<EntityHead<WXPayResultEntity>>() {
                    @Override
                    public void getBody(Call call, EntityHead<WXPayResultEntity> body) {
                        if (body.isSuccess()) {
                            WXPayResultEntity entity = body.getData();
                            IWXAPI api = WXAPIFactory.createWXAPI(RechargeActivity.this, C.WECHAT_APP_ID);
                            PayReq request = new PayReq();
                            request.appId = entity.getAppid();
                            request.partnerId = entity.getPartnerid();
                            request.prepayId = entity.getPrepayid();
                            request.packageValue = entity.getPackageX();
                            request.nonceStr = entity.getNoncestr();
                            request.timeStamp = entity.getTimestamp();
                            request.transaction = System.currentTimeMillis() + "";
                            request.sign = entity.getSign();
                            api.sendReq(request);
                            dismissLoading();
                        } else {
                            dismissLoading();
                            ToastUtil.showToast(body.getMsg());
                        }
                    }

                });
    }

    private void aliPay() {
        WealthApi api = DwRetrofit.getInstance().createApi(WealthApi.class);
        Call<EntityHead<String>> call = api.aliPayRecharge(accountInfo.getId(), 0, amount);
        LogUtils.v(call.request());
        call.enqueue(new DwCallback<EntityHead<String>>() {
            @Override
            public void getBody(Call call, final EntityHead<String> body) {
                if (body.isSuccess()) {
                    Runnable payRunnable = new Runnable() {
                        @Override
                        public void run() {
                            PayTask alipay = new PayTask(RechargeActivity.this);
                            Map<String, String> result = alipay.payV2(body.getData(), true);
                            Message msg = new Message();
                            msg.what = 0;
                            msg.obj = result;
                            mHandler.sendMessage(msg);
                        }
                    };

                    Thread payThread = new Thread(payRunnable);
                    payThread.start();
                } else {
                    ToastUtil.showToast(body.getMsg());
                }
            }
        });

    }

    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0: {
                    @SuppressWarnings("unchecked")
                    Map<String, String> result = (Map<String, String>) msg.obj;
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    LogUtils.d(result);
                    break;
                }
                case 1: {

                    break;
                }
                default:
                    break;
            }
        }
    };

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String countStr = charSequence.toString();
            if (!TextUtils.isEmpty(countStr)) {
                double count = 0;
                try {
                    count = Integer.parseInt(countStr);
                } catch (NumberFormatException e) {
                    ToastUtil.showToast("请不要输入奇怪的东西");
                }
                amount = (int) count;
                int min = AppConfig.IS_DEBUG ? 100 : 1000;
                if (count < min) {
                    tvPrice.setText("1000大洋起充哦");
                } else {
                    tvPrice.setText("￥" + String.format("%.2f", count / 100));
                }
            } else {
                tvPrice.setText("￥0.00");
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private View.OnClickListener onPayBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int min = AppConfig.IS_DEBUG ? 100 : 1000;
            if (amount < min) {
                return;
            }
            showLoading();
            switch (view.getId()) {
                case R.id.iv_wechat_pay: {
                    wechatPay();
                    break;
                }
                case R.id.iv_ali_pay: {
                    aliPay();
                    break;
                }
            }
        }
    };


}
