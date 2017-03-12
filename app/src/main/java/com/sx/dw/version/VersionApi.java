package com.sx.dw.version;

import com.sx.dw.core.network.EntityHead;

import java.io.File;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * @ClassName: ${CLASS_NAME}
 * @Description: 版本相关API
 * @author: fanjie
 * @date: 2016/9/8
 */
public interface VersionApi {

    String CHECK_APP_VERSION = "app/check/version";
    //    发送建议
    String SEND_ADVISE = "security/advise";

    @GET(CHECK_APP_VERSION)
    Call<EntityHead<UpdateInfo>> checkVersion(@Query("appType") String appType);

    @GET(SEND_ADVISE)
    Call<EntityHead> sendAdvise(@Query("dwID") String dwId,
                                @Query("type") String type,
                                @Query("voice") File file);
}
