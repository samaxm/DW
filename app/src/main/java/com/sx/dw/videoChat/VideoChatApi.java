package com.sx.dw.videoChat;

import com.sx.dw.accountAndSecurity.entity.BaseDisplayUserInfo;
import com.sx.dw.core.network.EntityHead;
import com.sx.dw.im.entity.LinkMan;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * @Description: 视频聊天相关API
 * @author: fanjie
 * @date: 2016/9/8
 */
public interface VideoChatApi {

    String MATCH_USER = "match/getMatch";
    // 匹配用户v2
    String MATCH_USER_V2 = "match/getMatch/v2";
    //    移除匹配
    String REMOVE_MATCH = "match/remove";
    // 获取段子
    String GET_JOKES = "padding/jokes";
    // 举报用户
    String REPORT_USER = "security/report";
    //    点赞、视频喜欢收藏：
    String SEND_LIKE = "match/like";
    //    统计正在匹配的人数
    String PING = "user/activity/ping";


    @GET(GET_JOKES)
    Call<EntityHead<List<String>>> getJokes(@Query("index") int index);

    @GET(REPORT_USER)
    Call<EntityHead> reportUser(@Query("reporterID") String reporterID,
                                @Query("reportedID") String reportedID,
                                @Query("reason") String reason);

    @GET(MATCH_USER)
    Call<EntityHead<LinkMan>> matchLinkMan(@Query("dwID") String dwID,
                                           @Query("icon") String icon,
                                           @Query("name") String name);

    @GET(MATCH_USER)
    Call<ResponseBody> matchLinkManWithResponseBody(@Query("dwID") String dwID,
                                                    @Query("icon") String icon,
                                                    @Query("name") String name);


    @GET(MATCH_USER_V2)
    Call<EntityHead<LinkMan>> matchLinkManV2(@Query("dwID") String dwID,
                                             @Query("icon") String icon,
                                             @Query("name") String name,
                                             @Query("isPrioritized") boolean isPrioritized);

    @GET(MATCH_USER_V2)
    Call<EntityHead<MatchResultEntity>> matchLinkManV2(@Query("dwID") String dwID,
                                                       @Query("icon") String icon,
                                                       @Query("name") String name,
                                                       @Query("sign") String sign,
                                                       @Query("tag") String tag,
                                                       @Query("isPrioritized") boolean isPrioritized);


    @GET(MATCH_USER_V2)
    Call<EntityHead<MatchResultEntity>> matchLinkManV2(@Query("dwID") String dwID,
                                             @Query("icon") String icon,
                                             @Query("name") String name,
                                             @Query("sign") String sign,
                                             @Query("isPrioritized") boolean isPrioritized);

    @GET(MATCH_USER_V2)
    Call<ResponseBody> matchLinkManV2WithResponseBody(@Query("dwID") String dwID,
                                                      @Query("icon") String icon,
                                                      @Query("name") String name,
                                                      @Query("sign") String sign,
                                                      @Query("isPrioritized") boolean isPrioritized);

    @GET(REMOVE_MATCH)
    Call<EntityHead> removeMatch(@Query("dwID") String dwID,
                                 @Query("icon") String icon,
                                 @Query("name") String name,
                                 @Query("sign") String sign,
                                 @Query("tag") String tag,
                                 @Query("isPrioritized") boolean isPrioritized);

    @GET(MATCH_USER_V2)
    Call<ResponseBody> testMatchUserV2(@Query("dwID") String dwID,
                                       @Query("icon") String icon,
                                       @Query("name") String name,
                                       @Query("isPrioritized") boolean isPrioritized);

    @GET(SEND_LIKE)
    Call<EntityHead<BaseDisplayUserInfo>> likeUser(@Query("dwID") String dwID,
                                                   @Query("likedID") String likedID);

    @GET(PING)
    Call<ResponseBody> ping(@Query("dwID") String dwID,
                            @Query("info") String info);


}
