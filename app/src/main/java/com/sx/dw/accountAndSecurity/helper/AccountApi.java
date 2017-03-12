package com.sx.dw.accountAndSecurity.helper;

import com.sx.dw.accountAndSecurity.entity.AccountInfoEntity;
import com.sx.dw.accountAndSecurity.entity.CheckVipCodeResultEntity;
import com.sx.dw.core.network.EntityHead;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * @Description: 账户相关Api,注册登录等
 * @author: fanjie
 * @date: 2016/9/8
 */
public interface AccountApi {

    // 用户注册
    String REGISTER = "user/register";
    //    用户注册2
    String REGISTER_V2 = "user/register/v2";
    //    用户登录
    String ACCOUNT_LOGIN = "user/activity/login";
    //    重置用户密码
    String RESET_USER_PASSWORD = "security/password";
    //    设置支付密码
    String SET_PAY_PSD = "security/pay_password";
    String CHECK_VIP_CODE = "user/register/vip/code";

    String USER_TAGS = "user/tags";

    /**
     * 旧版注册，注册信息的data直接作为json string 保存
     *
     * @param registerInfo
     * @param registerType
     * @return String
     */
    @GET(REGISTER)
    Call<EntityHead<String>> register(@Query("registerInfo") String registerInfo,
                                      @Query("registerType") String registerType);

    /**
     * 新版注册
     *
     * @param registerInfo
     * @param registerType
     * @return
     */
    @GET(REGISTER_V2)
    Call<EntityHead<AccountInfoEntity>> registerV2(@Query("registerInfo") String registerInfo,
                                                   @Query("registerType") String registerType);

    @GET(REGISTER_V2)
    Call<ResponseBody> testRegisterV2(@Query("registerInfo") String registerInfo,
                                      @Query("registerType") String registerType);


    /**
     * 账户登录
     * @param account
     * @param password MD5密码AES加密
     * @param accountType
     * @return
     */
    @GET(ACCOUNT_LOGIN)
    Call<EntityHead<AccountInfoEntity>> accountLogin(@Query("account") String account,
                                                     @Query("password") String password,
                                                     @Query("accountType") String accountType);

    /**
     * 重置用户密码
     * @param phoneNum
     * @param token
     * @param password
     * @return
     */
    @GET(RESET_USER_PASSWORD)
    Call<EntityHead> resetUserPassWord(@Query("phoneNum") String phoneNum,
                                       @Query("password") String password,
                                     @Query("token") String token);

    @GET(SET_PAY_PSD)
    Call<EntityHead> setPayPsd(@Query("dwID") String dwID,
                               @Query("password") String password,
                               @Query("token") String token);
    @GET(CHECK_VIP_CODE)
    Call<EntityHead<CheckVipCodeResultEntity>> checkVipCode(@Query("vipCode") String vipCode);

    @GET(USER_TAGS)
    Call<EntityHead<List<String>>> getUserTags(@Query("activityID") Integer activityID);

}
