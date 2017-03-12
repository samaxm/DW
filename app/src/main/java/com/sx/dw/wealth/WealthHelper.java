package com.sx.dw.wealth;

import android.text.TextUtils;
import android.widget.TextView;

import com.apkfuns.logutils.LogUtils;
import com.sx.dw.core.BaseActivity;
import com.sx.dw.core.DWApplication;
import com.sx.dw.core.network.DwCallback;
import com.sx.dw.core.network.DwRetrofit;
import com.sx.dw.core.network.EntityHead;
import com.sx.dw.core.util.ToastUtil;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.Map;

import retrofit2.Call;

import static com.sx.dw.core.GlobalData.accountInfo;
import static com.sx.dw.core.GlobalData.setAccountInfo;
import static com.umeng.socialize.utils.DeviceConfig.context;

/**
 * @Description: TODO 添加类的描述
 * @author: fanjie
 * @date: 2016/10/26 10:45
 */

public class WealthHelper {

    private static WealthHelper wealthHelper;
    /**
     * 上下文activity的引用，不可传给静态对象以免影响GC回收
     */
    private BaseActivity activity;
    private WealthApi wealthApi;
    private BindWechatCallback bindCallback;
    private UMShareAPI umApi;

    public static WealthHelper getInstance() {
        if (wealthHelper == null) {
            wealthHelper = new WealthHelper();
        }
        return wealthHelper;
    }

    public WealthHelper() {
        wealthApi = DwRetrofit.getInstance().createApi(WealthApi.class);
    }

    public void getUserWealth() {
        getUserWealth(null);
    }

    public void getUserWealth(final TextView tvWealth) {
        TokenEntity token = TokenUtil.getInstance().getToken();
        if (token == null || TextUtils.isEmpty(token.getKey())) {
            TokenUtil.getInstance().updateToken();
            return;
        }

        WealthApi api = DwRetrofit.getInstance().createApi(WealthApi.class);
        api.getWealth(accountInfo.getId(), token.getTokenStr()).enqueue(new DwCallback<EntityHead<Integer>>() {
            @Override
            public void getBody(Call call, EntityHead<Integer> body) {
                if (body.isSuccess()) {
                    accountInfo.setWealth(body.getData());
                    setAccountInfo();
                    if (tvWealth != null) {
                        tvWealth.setText(body.getData() + "");
                    }
                } else {
                    MobclickAgent.reportError(DWApplication.getInstance(), body.getMsg());
                    TokenUtil.getInstance().updateToken();
                }
            }

            @Override
            public void onFailure(Call<EntityHead<Integer>> call, Throwable t) {
                String tStr = "url = " + call.request().url() + "\n" + t;
                LogUtils.e(tStr);
                MobclickAgent.reportError(DWApplication.getInstance(), tStr);
//                FIXME Token可以作为判断是否多处登录的凭据，新登录的设备会将token更新，旧设备的token验证失败则提示在新地方登录
                TokenUtil.getInstance().updateToken();
            }
        });

    }

    public void bindWechat(BaseActivity activity, BindWechatCallback callback) {
        this.bindCallback = callback;
        this.activity = activity;
        umApi = UMShareAPI.get(activity);
        if (umApi.isInstall(activity, SHARE_MEDIA.WEIXIN)) {
            activity.showLoading("正在请求授权");
            umApi.doOauthVerify(activity, SHARE_MEDIA.WEIXIN, authListener);
        } else {
            ToastUtil.showToast("未安装应用程序");
        }
    }


    private UMAuthListener authListener = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
            umApi.getPlatformInfo(activity, SHARE_MEDIA.WEIXIN, getPlatformInfoListener);
        }

        @Override
        public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
            MobclickAgent.reportError(context, share_media + "");
            MobclickAgent.reportError(context, throwable);
            LogUtils.d("throwable =  " + throwable.toString());
            ToastUtil.showToast("授权失败");
            activity.dismissLoading();
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media, int i) {
            LogUtils.d("begin");
            ToastUtil.showToast("取消授权");
            activity.dismissLoading();
        }
    };

    private UMAuthListener getPlatformInfoListener = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
            wealthApi.bindWithdrawAccount(accountInfo.getId(), map.get("openid"), "WXPAY", TokenUtil.getInstance().getToken().getKey())
                    .enqueue(new DwCallback<EntityHead>() {
                        @Override
                        public void getBody(Call call, EntityHead body) {
                            if (body.isSuccess()) {
                                bindCallback.onSuccess();
                            } else {
                                activity.dismissLoading();
                                ToastUtil.showToast(body.getMsg());
                            }
                        }
                    });
        }

        @Override
        public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
            MobclickAgent.reportError(context, share_media + "");
            MobclickAgent.reportError(context, throwable);
            ToastUtil.showToast("授权失败");
            activity.dismissLoading();
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media, int i) {
            ToastUtil.showToast("取消授权");
            activity.dismissLoading();
        }
    };

    interface BindWechatCallback {
        void onSuccess();
    }

}
