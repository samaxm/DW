package com.sx.dw.accountAndSecurity.helper;

import android.content.Context;
import android.text.TextUtils;

import com.apkfuns.logutils.LogUtils;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.sx.dw.accountAndSecurity.entity.AccountInfoEntity;
import com.umeng.analytics.MobclickAgent;

import java.util.HashSet;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import static com.sx.dw.core.GlobalData.accountInfo;
import static com.sx.dw.core.GlobalData.getClient;
import static com.sx.dw.core.GlobalData.setAccountInfo;

/**
 * @Description: 登录帮助实现类
 * @author: fanjie
 * @date: 2016/9/19
 */
public class LoginHelperImpl implements ILoginHelper {


    private Context context;

    public LoginHelperImpl(Context context) {
        this.context = context;
    }

    @Override
    public boolean isHXLoggedIn() {
        return EMClient.getInstance().isLoggedInBefore();
    }

    @Override
    public void loginHX(final AccountInfoEntity entity, final ILoginHelper.LoginCallback callback) {
        if(isHXLoggedIn()){
            getClient().logout(true);
        }
        if (entity != null) {
            String account = entity.getId();
            String password = entity.getPassword();
            if (TextUtils.isEmpty(account) || TextUtils.isEmpty(password)) {
                LogUtils.d(entity.toJsonString());
                callback.onError("账号或密码为空");
            } else {
                EMClient.getInstance().login(account, password, new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        setAccountInfo(entity);
                        JPushInterface.setAlias(context, accountInfo.getId(), new TagAliasCallback() {
                            @Override
                            public void gotResult(int i, String s, Set<String> set) {

                            }
                        });
                        Set<String> strings = new HashSet<>();
                        strings.add("ONLINE_NOTICE");
                        JPushInterface.setTags(context, strings, new TagAliasCallback() {
                            @Override
                            public void gotResult(int i, String s, Set<String> set) {

                            }
                        });
                        callback.onSuccess();
                    }

                    @Override
                    public void onError(int i, String s) {
                        LogUtils.d("message = " + s + "\n entity.toJsonString() = " + entity.toJsonString());
                        MobclickAgent.reportError(context, "message = " + s + "\n entity.toJsonString() = " + entity.toJsonString());
                        callback.onError(s);
                    }

                    @Override
                    public void onProgress(int i, String s) {

                    }
                });
            }
        } else {
            callback.onError("账户对象为空");
            setAccountInfo(null);

        }
    }

}
