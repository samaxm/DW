package com.sx.dw.core.network;

import com.apkfuns.logutils.LogUtils;
import com.google.gson.GsonBuilder;
import com.sx.dw.core.AppConfig;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @Description: Retrofit，获取实例用
 * @author: fanjie
 * @date: 2016/9/22
 */

public class DwRetrofit {
    private static DwRetrofit dwRetrofit;
    private Retrofit retrofit;

    public static String BASE_URL;

    static {
        if (AppConfig.IS_CLOUD) {
            // 云端
//            BASE_URL = "http://dev.service.dawan.online/face2face/";
            BASE_URL = "http://www.sxyusx.com/face2face/";
        } else {
            // 本地端
            BASE_URL = "http://192.168.1.198:7777/face2face/";
        }
    }

    public static DwRetrofit getInstance() {
        if (dwRetrofit == null) {
            dwRetrofit = new DwRetrofit();
        }
        return dwRetrofit;
    }

    private DwRetrofit() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
//                添加自定义的解析工厂，暂时有许多问题
//                .addConverterFactory(new NullOnEmptyConverterFactory())
                // FIXME: 2016/10/27 这里为了防止数据库model的静态属性导致解析错误
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().excludeFieldsWithModifiers(Modifier.FINAL, Modifier.STATIC).create()))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public <T> T createApi(Class<T> clazz) {
        return retrofit.create(clazz);
    }

    // TODO: 2016/10/27 重写一个解析工厂，模仿官方写法，但降低一些安全性的东西
    class NullOnEmptyConverterFactory extends Converter.Factory {
        @Override
        public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
            final Converter<ResponseBody, ?> delegate = retrofit.nextResponseBodyConverter(this, type, annotations);
            return new Converter<ResponseBody, Object>() {
                @Override
                public Object convert(ResponseBody body) throws IOException {
                    if (body.contentLength() == 0) {
                        LogUtils.d("null");
                        return null;
                    }
                    LogUtils.d(delegate.convert(body));
                    return delegate.convert(body);
                }
            };
        }
    }

}
