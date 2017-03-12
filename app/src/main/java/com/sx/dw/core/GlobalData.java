package com.sx.dw.core;

import android.content.SharedPreferences;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.apkfuns.logutils.LogUtils;
import com.hyphenate.chat.EMCallManager;
import com.hyphenate.chat.EMClient;
import com.sx.dw.accountAndSecurity.entity.AccountInfoEntity;
import com.sx.dw.accountAndSecurity.entity.ActivityDetail;
import com.sx.dw.accountAndSecurity.entity.ActivityList;
import com.sx.dw.im.entity.Chat;
import com.sx.dw.im.entity.LinkMan;
import com.sx.dw.im.helper.DBUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * @Description:
 * @author: fanjie
 * @date: 2016/10/12 19:01
 */

public class GlobalData {

    public static HashMap<String, Chat> chatMap;
    public static HashMap<String, LinkMan> linkManMap;
    public static Long dateNum;
    public static List<ActivityDetail> activityLists;

    public static AccountInfoEntity accountInfo;
    public static EMCallManager callManager ;
    public static EMClient client ;
    private static final String SP_FILE_NAME_USER_ACCOUNT_INFO = "SP_FILE_NAME_USER_ACCOUNT_INFO";
    private static final String SP_KEY_USER_ACCOUNT_INFO = "SP_KEY_USER_ACCOUNT_INFO";
    private static final String SP_KEY_ACTIVITY = "SP_KEY_ACTIVITY";
    private static final String SP_KEY_ACTIVITY_DATENUM = "DATENUM";
    private static final String SP_KEY_ACTIVITY_ACTIVITIES = "ACTIVITIES";
    private static final String DW_TOKEN = "dw_token";


    private static SharedPreferences sharedPreferences;


    private static SharedPreferences sharedPreferences_activity;



    public volatile static boolean inChatRoom = false;
    public volatile static boolean inChat = false;



    static {
        sharedPreferences = DWApplication.getInstance().getSharedPreferences(SP_FILE_NAME_USER_ACCOUNT_INFO, 0);
        sharedPreferences_activity = DWApplication.getInstance().getSharedPreferences(SP_KEY_ACTIVITY, 0);
        accountInfo = getAccountInfoEntity();
        updateChats();
        updateLinkMans();
    }


    public static Long getDateNum(){
        if(dateNum==null){
            dateNum=sharedPreferences_activity.getLong(SP_KEY_ACTIVITY_DATENUM,0);
        }
        return dateNum;
    }


    public static void updateActivityList(ActivityList list){
        SharedPreferences.Editor editor=sharedPreferences_activity.edit();
        editor.putLong(SP_KEY_ACTIVITY_DATENUM,list.getDateNum());
        editor.putString(SP_KEY_ACTIVITY_ACTIVITIES,JSON.toJSONString(list.getList()));
        editor.commit();
    }


    public static ActivityDetail getLatestActivity(){
        if(activityLists==null){
            String json=sharedPreferences_activity.getString(SP_KEY_ACTIVITY_ACTIVITIES,"[]");
            activityLists=JSON.parseArray(json,ActivityDetail.class);
            Collections.sort(activityLists);
        }
        Calendar calendar=Calendar.getInstance();
        long current=calendar.getTimeInMillis();
        ActivityDetail latest=null;
        for(ActivityDetail detail:activityLists){
            if(detail.getBegintime()<=current&&detail.getEndtime()>current){
                latest=detail;
                break;
            }else if(detail.getBegintime()>current) {
                latest=detail;
                break;
            }
        }
        //若无活动则直接可进入视频页面
        return latest;
    }
    public static EMClient getClient(){
        if(client==null){
            client=EMClient.getInstance();
        }
        return client;
    }
    public static EMCallManager getCallManager(){
        if(callManager==null){
            callManager=getClient().callManager();
        }
        return callManager;
    }

    public static void setAccountInfo() {
        setAccountInfo(accountInfo);
    }

    /**
     * @param entity 传入null则清空账户信息
     */
    public static void setAccountInfo(AccountInfoEntity entity) {
        LogUtils.d(entity);
        String info;
        if (entity == null) {
            info = "";
            accountInfo = null;
        } else {
            info = entity.toJsonString();
        }
        LogUtils.v(info);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SP_KEY_USER_ACCOUNT_INFO, info);
        editor.commit();
        accountInfo = getAccountInfoEntity();
    }


    public static void updateLinkMans() {
        linkManMap = new HashMap<>();
        List<LinkMan> linkMans = DBUtils.getInstance().getLinkMans();
        for (LinkMan linkMan : linkMans) {
            linkManMap.put(linkMan.getDwID(), linkMan);
        }
    }

    public static void updateChats() {
        chatMap = new HashMap<>();
        List<Chat> chats = DBUtils.getInstance().getChats();
        for (Chat c : chats) {
            chatMap.put(c.getLinkManId(), c);
        }

    }

    private static AccountInfoEntity getAccountInfoEntity() {
        String jsonString = sharedPreferences.getString(SP_KEY_USER_ACCOUNT_INFO, "");
        LogUtils.v("jsonString = " + jsonString);
        if (TextUtils.isEmpty(jsonString)) {
            return null;
        }
        AccountInfoEntity entity = null;
        try {
            entity = JSON.parseObject(jsonString, AccountInfoEntity.class);
        } catch (Exception e) {
            LogUtils.e(e);
            MobclickAgent.reportError(DWApplication.getInstance(), e);
        }
        LogUtils.v("entity = " + entity);
        return entity;
    }
}
