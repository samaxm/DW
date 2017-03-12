package com.sx.dw.accountAndSecurity.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;

import com.alipay.sdk.app.PayTask;
import com.apkfuns.logutils.LogUtils;
import com.sx.dw.R;
import com.sx.dw.core.BaseActivity;
import com.sx.dw.core.network.DwCallback;
import com.sx.dw.core.network.DwRetrofit;
import com.sx.dw.core.network.EntityHead;
import com.sx.dw.core.util.C;
import com.sx.dw.core.util.ToastUtil;
import com.sx.dw.wealth.WXPayResultEntity;
import com.sx.dw.wealth.WealthApi;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.util.Map;

import retrofit2.Call;

import static com.sx.dw.core.util.C.IntentExtra.DW_ID;
import static com.sx.dw.core.util.C.IntentExtra.VIP_CODE;

/**
 * vip支付页面
 */
public class PayVipChargeActivity extends BaseActivity {

    private WealthApi wealthApi;
    private String dwID;
    private String vipCode;

    private VipPayReceiver receiver;
    private IntentFilter filter;

    public static void startMe(Activity activity, String dwID, String vipCode, int requestCode) {
        Intent intent = new Intent(activity, PayVipChargeActivity.class);
        intent.putExtra(VIP_CODE, vipCode);
        intent.putExtra(C.IntentExtra.REQUEST_CODE, requestCode);
        intent.putExtra(DW_ID, dwID);
        activity.startActivityForResult(intent, requestCode);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_vip_charge);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        wealthApi = DwRetrofit.getInstance().createApi(WealthApi.class);
        dwID = getIntent().getStringExtra(DW_ID);
        vipCode = getIntent().getStringExtra(VIP_CODE);

        if (receiver == null || filter == null) {
            filter = new IntentFilter();
            filter.addAction(C.BROADCAST_ACTION_VIP_PAY);
            receiver = new VipPayReceiver(new VipPayReceiver.VipPayCallback() {
                @Override
                public void paySuccess() {
                    dismissLoading();
                    ToastUtil.showToast("支付成功，请注册");
                    RegisterActivity.startMe(PayVipChargeActivity.this,vipCode,C.RequestCode.VIP_REGISTER);
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

    public void onWeChatClick(View view) {
        showLoading();
        wealthApi.wxPayVip(dwID, vipCode, 1).enqueue(new DwCallback<EntityHead<WXPayResultEntity>>(this) {
            @Override
            public void getBody(Call call, EntityHead<WXPayResultEntity> body) {
                if (body.isSuccess()) {
                    WXPayResultEntity entity = body.getData();
                    IWXAPI api = WXAPIFactory.createWXAPI(PayVipChargeActivity.this, C.WECHAT_APP_ID);
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

    public void onAliPayClick(View view) {
        showLoading();
        wealthApi.aliPayVip(dwID, vipCode, 0).enqueue(new DwCallback<EntityHead<String>>() {
            @Override
            public void getBody(Call call, final EntityHead<String> body) {
                if (body.isSuccess()) {
                    Runnable payRunnable = new Runnable() {
                        @Override
                        public void run() {
                            PayTask alipay = new PayTask(PayVipChargeActivity.this);
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
}
