package com.sx.dw.wxapi;

import android.os.Bundle;

import com.apkfuns.logutils.LogUtils;
import com.sx.dw.core.BaseActivity;
import com.sx.dw.core.util.C;
import com.sx.dw.core.util.ToastUtil;
import com.sx.dw.wealth.WealthHelper;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

public class WXPayEntryActivity extends BaseActivity implements IWXAPIEventHandler {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_wxpay_entry);
//        LogUtils.d("onCreate");
    }

    @Override
    public void onReq(BaseReq baseReq) {
        LogUtils.d("onReq");
    }

    @Override
    public void onResp(BaseResp resp) {
        switch (resp.errCode) {
            case 0: {
                ToastUtil.showToast("支付成功，稍后到账！");
                WealthHelper.getInstance().getUserWealth();
                setResult(C.ResultCode.WXPAY_SUCCESS);
                break;
            }
            case -1:
                ToastUtil.showToast("支付失败");
                break;
            case -2:
                ToastUtil.showToast("取消支付");
                break;
        }
        finish();
    }
}
