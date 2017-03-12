package com.sx.dw.accountAndSecurity.entity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import static com.sx.dw.core.GlobalData.getLatestActivity;

/**
 * @Description: TODO 添加类的描述
 * @author: fanjie
 * @date: 2017/1/5 9:34
 */

public class ActivityDetail implements Comparable<ActivityDetail>,Parcelable{


    private Integer id;
    private String imgUrl;
    private Long begintime;
    private Long endtime;
    private String description;
    private Long datenum;
    public static String ActivityBundle="ActivityBundle";
    public static String ActivityKey="activity";

    public ActivityDetail(){};

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Long getBegintime() {
        return begintime;
    }

    public void setBegintime(Long begintime) {
        this.begintime = begintime;
    }

    public Long getEndtime() {
        return endtime;
    }

    public void setEndtime(Long endtime) {
        this.endtime = endtime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getDatenum() {
        return datenum;
    }

    public void setDatenum(Long datenum) {
        this.datenum = datenum;
    }

    @Override
    public int compareTo(ActivityDetail a) {
        if(a.getBegintime()>this.begintime){
            return -1;
        }else if(a.getBegintime()==this.begintime){
            return 0;
        }else {
            return 1;
        }
    }



    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ActivityDetail> CREATOR = new Creator<ActivityDetail>() {
        @Override
        public ActivityDetail createFromParcel(Parcel in) {
            return new ActivityDetail(in);
        }

        @Override
        public ActivityDetail[] newArray(int size) {
            return new ActivityDetail[size];
        }
    };


    protected ActivityDetail(Parcel in) {
        id=in.readInt();
        imgUrl=in.readString();
        begintime=in.readLong();
        endtime=in.readLong();
        description=in.readString();
        datenum=in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(imgUrl);
        dest.writeLong(begintime);
        dest.writeLong(endtime);
        dest.writeString(description);
        dest.writeLong(datenum);
    }

    public static Intent createInentWithActivity(Context context,Class c){
        Intent intent=new Intent(context,c);
        ActivityDetail detail=getLatestActivity();
        Bundle bundle=new Bundle();
        bundle.putParcelable(ActivityKey,detail);
        intent.putExtra(ActivityBundle,bundle);
        return intent;
    }

    public static Intent createInentWithActivity(Context context,Class c,ActivityDetail detail){
        Intent intent=new Intent(context,c);
        Bundle bundle=new Bundle();
        bundle.putParcelable(ActivityKey,detail);
        intent.putExtra(ActivityBundle,bundle);
        return intent;
    }


    public static ActivityDetail getActivityDetail(Intent intent){
        Bundle bundle=intent.getBundleExtra(ActivityBundle);
        if(bundle!=null){
            return bundle.getParcelable(ActivityKey);
        }else
            return null;
    }
}
