package com.sx.dw.social;

import com.sx.dw.accountAndSecurity.entity.AccountInfoEntity;
import com.sx.dw.core.network.EntityHead;
import com.sx.dw.im.entity.LinkMan;
import com.sx.dw.social.entity.LikeRecordEntity;
import com.sx.dw.social.entity.SearchUserBean;

import java.io.File;
import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * @ClassName: ${CLASS_NAME}
 * @Description: 用户信息API，设置用户信息、获取用户信息等
 * @author: fanjie
 * @date: 2016/9/8
 */
public interface UserInfoApi {

    // 获取用户基本信息
    String GET_USER_INFO = "user/info";
    //    设置用户信息
    String SET_USER_INFO = "user/set/info";
    //    上传头像
    String SET_USER_ICON = "/user/set/icon";
    //    获取点赞记录、收藏列表
    String GET_LIKE_LIST = "match/like/records";

    String SEARCH_USER="search/user";


    @GET(GET_USER_INFO)
    Call<EntityHead<LinkMan>> getUserInfo(@Query("dwID") String dwID);

    @GET(GET_USER_INFO)
    Call<ResponseBody> testGetUserInfo(@Query("dwID") String dwID);


    @Multipart
    @POST(SET_USER_INFO)
    Call<EntityHead<AccountInfoEntity>> setUserInfo(@Query("password") String password,
                                                    @Query("dwID") String dwID,
                                                    @Query("info") String info,
                                                    @Part("file") RequestBody file,
                                                    @Query("iconIndex") int iconIndex);
    @POST(SET_USER_INFO)
    Call<EntityHead<AccountInfoEntity>> setUserInfo(@Query("password") String password,
                                                    @Query("dwID") String dwID,
                                                    @Query("info") String info);


    @GET(SET_USER_ICON)
    Call<EntityHead> setUserIcon(@Query("password") String password,@Query("dwID") String dwID,
                                 @Query("file") File file);

    @GET(GET_LIKE_LIST)
    Call<EntityHead<List<LikeRecordEntity>>> getLikeList(@Query("dwID") String dwID);

    @GET(GET_LIKE_LIST)
    Call<ResponseBody> testGetLikeList(@Query("dwID") String dwID);

    @GET(SEARCH_USER)
    Call<EntityHead<List<SearchUserBean>>> searchUser(@Query("keyword") String keyword,@Query("page") Integer page);

}
