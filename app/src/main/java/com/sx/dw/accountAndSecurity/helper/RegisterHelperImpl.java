package com.sx.dw.accountAndSecurity.helper;

import android.content.Intent;
import android.text.TextUtils;

import com.apkfuns.logutils.LogUtils;
import com.sx.dw.accountAndSecurity.entity.AccountInfoEntity;
import com.sx.dw.accountAndSecurity.entity.PhoneRegisterRequestEntity;
import com.sx.dw.accountAndSecurity.entity.RegisterRequestEntity;
import com.sx.dw.accountAndSecurity.entity.VipRegisterRequestEntity;
import com.sx.dw.core.BaseActivity;
import com.sx.dw.core.network.DwCallback;
import com.sx.dw.core.network.DwRetrofit;
import com.sx.dw.core.network.EntityHead;
import com.sx.dw.core.util.MD5;
import com.sx.dw.core.util.ToastUtil;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.Map;

import retrofit2.Call;

import static com.sx.dw.core.GlobalData.setAccountInfo;

/**
 * @Description: 注册业务帮助类
 * @author: fanjie
 * @date: 2016/9/22
 */

public class RegisterHelperImpl implements IRegisterHelper {

    private BaseActivity context;
    private UMShareAPI umApi;
    private AccountApi accountApi;

    private SHARE_MEDIA platform;
    private RegisterCallback authCallback;

    private String vipCode;


    public RegisterHelperImpl(BaseActivity context) {
        this.context = context;
        accountApi = DwRetrofit.getInstance().createApi(AccountApi.class);
        umApi = UMShareAPI.get(context);
    }

    @Override
    public void authRegister(SHARE_MEDIA platform, RegisterCallback callback) {
        authRegister(platform,null,callback);
    }

    @Override
    public void authRegister(SHARE_MEDIA platform, String vipCode, RegisterCallback callback) {
        this.platform = platform;
        this.authCallback = callback;
        this.vipCode = vipCode;
        if (umApi.isInstall(context, platform)) {
            context.showLoading("正在请求授权");
            umApi.doOauthVerify(context, platform, authListener);
        } else {
            ToastUtil.showToast("未安装应用程序");
        }
    }

    /**
     * 在调用友盟授权时必须在onActivityResult中调用
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        umApi.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void registerByPhone(PhoneRegisterRequestEntity entity, final RegisterCallback callback) {
        LogUtils.d(entity);
        accountApi.registerV2(entity.toJsonString(), "PHONECODE").enqueue(new DwCallback<EntityHead<AccountInfoEntity>>() {
            @Override
            public void getBody(Call call, EntityHead<AccountInfoEntity> body) {
                if (body.isSuccess()) {
                    LogUtils.d(body.getData());
                    callback.onSuccess(body.getData());
                } else {
                    callback.onFailure(body.getMsg());
                }
            }

            @Override
            public void onFailure(Call<EntityHead<AccountInfoEntity>> call, Throwable t) {
                super.onFailure(call, t);
                callback.onFailure(t.getMessage());
            }
        });
    }




    private UMAuthListener authListener = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
            if (umApi != null && platform != null) {
                umApi.getPlatformInfo(context, platform, getPlatformInfoListener);
            }

        }

        @Override
        public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
            MobclickAgent.reportError(context, share_media + "");
            MobclickAgent.reportError(context, throwable);
            LogUtils.d("throwable =  " + throwable.toString());
            authCallback.onFailure("授权失败");
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media, int i) {
            LogUtils.d("begin");
            authCallback.onFailure("取消授权");
        }
    };

    private UMAuthListener getPlatformInfoListener = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
            map.put("registerChannel",platform.name());
            String jsonStr = RegisterRequestEntity.toRegisterJson(map, share_media == SHARE_MEDIA.WEIXIN);
            LogUtils.d(jsonStr);
            String info;
            String type;
            if(TextUtils.isEmpty(vipCode)){
                info = jsonStr;
                type = "INFO";
            }else {
                VipRegisterRequestEntity vipEntity = new VipRegisterRequestEntity();
                vipEntity.setCode(vipCode);
                vipEntity.setFormat(VipRegisterRequestEntity.INFO);
                vipEntity.setRegisterInfo(jsonStr);
                info = vipEntity.toJsonString();
                type = "VIP";
            }
            accountApi.registerV2(info, type).enqueue(authRegisterCallback);

        }

        @Override
        public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
            authCallback.onFailure("授权失败");
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media, int i) {
            authCallback.onFailure("取消授权");
        }
    };

    private DwCallback<EntityHead<AccountInfoEntity>> authRegisterCallback = new DwCallback<EntityHead<AccountInfoEntity>>() {
        @Override
        public void getBody(Call call, EntityHead<AccountInfoEntity> body) {
            if (body.isSuccess()) {
                LogUtils.d("response.body().getData() = " + body.getData());
                AccountInfoEntity entity = body.getData();
                if (TextUtils.isEmpty(entity.getPassword())) {
                    entity.setPassword(MD5.GetMD5Code(entity.getUnionid()));
                }
                if(platform == SHARE_MEDIA.WEIXIN){
                    entity.setLoginType(AccountInfoEntity.LoginType.WECHAT);
                }else if(platform == SHARE_MEDIA.QQ){
                    entity.setLoginType(AccountInfoEntity.LoginType.QQ);
                }
                setAccountInfo(entity);
                authCallback.onSuccess(entity);

            } else {
                MobclickAgent.reportError(context, body.getMsg());
                authCallback.onFailure(body.getMsg());
            }
        }

        @Override
        public void onFailure(Call<EntityHead<AccountInfoEntity>> call, Throwable t) {
            super.onFailure(call, t);
            authCallback.onFailure(t.getMessage());
        }
    };



}
