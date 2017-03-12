package com.sx.dw.core.network;

import com.sx.dw.core.AppConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.protobuf.ProtoConverterFactory;

/**
 * @Description: 消息api构建器
 * @author: fanjie
 * @date: 2016/10/8 14:04
 */

public class MsgRetrofit {

    private static MsgRetrofit msgRetrofit;
    private Retrofit retrofit;

    static String MSG_BASE_UTL;

    static {
        if (AppConfig.IS_CLOUD) {
//            MSG_BASE_UTL = "http://dev.service.dawan.online/message/";
            MSG_BASE_UTL = "http://www.sxyusx.com/message2/";
        } else {
            MSG_BASE_UTL = "http://192.168.1.198:8080/message/";
        }
    }

    public static MsgRetrofit getInstance() {
        if (msgRetrofit == null) {
            msgRetrofit = new MsgRetrofit();
        }
        return msgRetrofit;
    }

    private MsgRetrofit() {
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(1, TimeUnit.MINUTES)
                .connectTimeout(1, TimeUnit.MINUTES)
                .build();
        retrofit = new Retrofit.Builder()
                .baseUrl(MSG_BASE_UTL)
                .addConverterFactory(ProtoConverterFactory.create())
                .client(client)
                .build();
    }

    public <T> T createApi(Class<T> clazz) {
        return retrofit.create(clazz);
    }

}
