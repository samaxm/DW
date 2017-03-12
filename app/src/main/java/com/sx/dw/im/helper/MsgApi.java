package com.sx.dw.im.helper;

import com.sx.dw.im.message.MessageProtos;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * @Description: 消息相关api
 * @author: fanjie
 * @date: 2016/9/30 15:46
 */

public interface MsgApi {


    @GET("sync")
    Call<MessageProtos.Message> sync(@Query("syncNum") long syncNum,
                                     @Query("dwID") String dwID);
    @GET("sync")
    Call<ResponseBody> testSync(@Query("syncNum") long syncNum,
                                     @Query("dwID") String dwID);

    @POST("send")
    Call<ResponseBody> testSend(@Body MultipartBody body);

    @POST("send")
    Call<MessageProtos.Message> send(@Body MultipartBody body);

}
