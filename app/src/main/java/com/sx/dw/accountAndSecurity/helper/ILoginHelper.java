package com.sx.dw.accountAndSecurity.helper;

import com.sx.dw.accountAndSecurity.entity.AccountInfoEntity;

/**
 * @Description: 登录帮助类
 * @author: fanjie
 * @date: 2016/9/22
 */

public interface ILoginHelper {
    boolean isHXLoggedIn();
    void loginHX(AccountInfoEntity entity, LoginCallback callback);

    interface LoginCallback{
        void onSuccess();
        void onError(String msg);
    }
}
