package com.sx.dw.wealth;

import com.sx.dw.core.network.EntityHead;
import com.sx.dw.videoChat.TipResultEntity;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * @Description: TODO 添加类的描述
 * @author: fanjie
 * @date: 2016/10/10 12:57
 */

public interface WealthApi {

    //    充值
    String RECHARGE = "wealth/recharge";
    //    获取用户财富
    String GET_WEALTH = "wealth";
    //    获取公钥
    String GET_RSA = "security/key/get";
    //    上传私钥
    String UPLOAD_AES = "security/key/upload";
    //    绑定提现账户（微信）
    String BIND_WITHDRAW_ACCOUNT = "user/bind/account";
    String WITHDRAW = "wealth/withdraw";
    String SET_WORTH = "user/set/worth";
    String PAY_VIP = "user/register/vip/pay";
    String TIP = "wealth/tip";
    int KEY_ERROR = 10002;


    @GET(RECHARGE)
    Call<EntityHead<String>> aliPayRecharge(@Query("dwID") String dwID,
                                            @Query("channel") int channel,
                                            @Query("amount") int amount);

    @GET(RECHARGE)
    Call<EntityHead<WXPayResultEntity>> wxPayRecharge(@Query("dwID") String dwID,
                                                      @Query("channel") int channel,
                                                      @Query("amount") int amount);

    @GET(GET_WEALTH)
    Call<EntityHead<Integer>> getWealth(@Query("dwID") String dwID,
                                        @Query("token") String token);

    @GET(GET_RSA)
    Call<EntityHead<String>> getRSA(@Query("dwID") String dwID);

    @GET(UPLOAD_AES)
    Call<EntityHead> uploadAES(@Query("dwID") String dwID,
                               @Query("password") String password,
                               @Query("key") String key);

    @GET(BIND_WITHDRAW_ACCOUNT)
    Call<EntityHead> bindWithdrawAccount(@Query("dwID") String dwID,
                                         @Query("account") String account,
                                         @Query("accountType") String accountType,
                                         @Query("token") String token);

    @GET(WITHDRAW)
    Call<EntityHead> withdraw(@Query("dwID") String dwID,
                              @Query("pay_password") String pay_password,
                              @Query("amount") String amount);

    @GET(SET_WORTH)
    Call<EntityHead<Integer>> setWorth(@Query("dwID") String dwID,
                                    @Query("paypassword") String pay_password,
                                    @Query("worth") String amount);


    @GET(PAY_VIP)
    Call<EntityHead<WXPayResultEntity>> wxPayVip(@Query("dwID") String dwID,
                                @Query("vipCode") String vipCode,
                                @Query("channel") int channel);
    @GET(PAY_VIP)
    Call<EntityHead<String>> aliPayVip(@Query("dwID") String dwID,
                                @Query("vipCode") String vipCode,
                                @Query("channel") int channel);
    @GET(TIP)
    Call<EntityHead<TipResultEntity>> tip(@Query("dwID") String dwID,
                                          @Query("toID") String toID,
                                          @Query("amount") int amount);

}
