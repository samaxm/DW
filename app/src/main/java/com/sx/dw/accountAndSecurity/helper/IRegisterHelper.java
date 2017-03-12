package com.sx.dw.accountAndSecurity.helper;

import android.content.Intent;

import com.sx.dw.accountAndSecurity.entity.AccountInfoEntity;
import com.sx.dw.accountAndSecurity.entity.PhoneRegisterRequestEntity;
import com.umeng.socialize.bean.SHARE_MEDIA;

/**
 * @Description: 注册帮助类
 * @author: fanjie
 * @date: 2016/9/22
 */

public interface IRegisterHelper {
    void authRegister (SHARE_MEDIA platform,RegisterCallback callback);
    void onActivityResult(int requestCode, int resultCode, Intent data);

    void registerByPhone(PhoneRegisterRequestEntity entity,RegisterCallback callback);

    void authRegister(SHARE_MEDIA platform, String vipCode, RegisterCallback authRegisterCallback);


    interface RegisterCallback {
        void onSuccess(AccountInfoEntity entity);
        void onFailure(String msgToUser);
    }

    interface SendCodeCallback{
        void onSuccess();
        void onFailure(String msgToUser);
    }
}
