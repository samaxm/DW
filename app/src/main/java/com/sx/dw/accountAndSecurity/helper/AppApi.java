package com.sx.dw.accountAndSecurity.helper;

import com.sx.dw.accountAndSecurity.entity.ActivityList;
import com.sx.dw.accountAndSecurity.entity.AnswerResponse;
import com.sx.dw.accountAndSecurity.entity.WebGame;
import com.sx.dw.core.network.EntityHead;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * @Description: TODO 添加类的描述
 * @author: fanjie
 * @date: 2017/1/5 9:31
 */

public interface AppApi {

    static String GET_ACTIVITY="app/activities";
    static String CHECK_ACTIVITY_ANSWER="app/activity/answer";
    static String UPLOAD_ADDRESS="app/activity/address";
    static String WEB_GAME="app/game";


    @GET(GET_ACTIVITY)
    Call<EntityHead<ActivityList>> getTodayActivity(@Query("dateNum") Long dateNum);
    @GET(WEB_GAME)
    Call<EntityHead<WebGame>> getWebGame();

    @GET(CHECK_ACTIVITY_ANSWER)
    Call<EntityHead<AnswerResponse>> checkActivityAnswer(@Query("dwID") String dwID, @Query("answer") String answer, @Query("activityID") Integer activityID);

    @GET(UPLOAD_ADDRESS)
    Call<EntityHead<AnswerResponse>> uploadAddress(@Query("answerID") Integer answerID,@Query("dwID") String dwID,@Query("address") String address);
}
