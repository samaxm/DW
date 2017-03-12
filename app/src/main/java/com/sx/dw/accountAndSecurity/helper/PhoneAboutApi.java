package com.sx.dw.accountAndSecurity.helper;

import com.sx.dw.core.network.EntityHead;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * @Description: 手机相关API，发送验证码等等
 * @author: fanjie
 * @date: 2016/9/24
 */

public interface PhoneAboutApi {

    //    绑定手机号
    String BIND_PHONE_NUMBER = "user/bind/phone";
    //    验证手机号
    String CHECK_FORGET_PASSWORD_PHONE_CODE = "security/password/token/check";
    //    发送手机验证码
    String SEND_REGISTER_PHONE_CODE = "user/register/send/code";
    //    发送忘记密码验证码
    String SEND_FORGET_PASSWORD_PHONE_CODE = "security/password/token";
    //    发送设置支付密码验证码
    String SEND_SET_PAY_PSD_CODE = "security/pay_password/token";
    //    验证设置支付密码验证码
    String CHECK_SET_PAY_PSD_CODE = "security/pay_password/token/check";


    @GET(SEND_REGISTER_PHONE_CODE)
    Call<EntityHead> sendRegisterPhoneCode(@Query("phoneNum") String phoneNum);


    @GET(SEND_FORGET_PASSWORD_PHONE_CODE)
    Call<EntityHead> sendForgetPasswordPhoneCode(@Query("phoneNum") String phoneNum);

    @GET(CHECK_FORGET_PASSWORD_PHONE_CODE)
    Call<EntityHead> checkForgetPasswordPhoneCode(@Query("phoneNum") String phoneNum,
                                                  @Query("phoneCode") String phoneCode);

    @GET(BIND_PHONE_NUMBER)
    Call<EntityHead> bindPhoneNumber(@Query("dwID") String dwID,
                                     @Query("phoneNum") String phoneNum,
                                     @Query("code") String code,
                                     @Query("isNotRegister") Boolean isNotRegister);

    @GET(SEND_SET_PAY_PSD_CODE)
    Call<EntityHead> sendSetPayPsdCode(@Query("dwID") String dwID,
                                       @Query("phoneNum") String phoneNum);

    @GET(CHECK_SET_PAY_PSD_CODE)
    Call<EntityHead> checkSetPayPsdCode(@Query("dwID") String dwID,
                                        @Query("phoneNum") String phoneNum,
                                        @Query("code") String code);

}
