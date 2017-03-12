package com.sx.dw.accountAndSecurity.entity;

import com.alibaba.fastjson.JSON;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * @Description: 注册请求参数封装
 * @author: fanjie
 * @date: 2016/9/12
 */
public class RegisterRequestEntity {
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("sex")
    @Expose
    private int sex;
    @SerializedName("openid")
    @Expose
    private String openid;
    @SerializedName("unionid")
    @Expose
    private String unionid;
    @SerializedName("icon")
    @Expose
    private String icon;
    @SerializedName("area")
    @Expose
    private String area;
    @SerializedName("registerChannel")
    @Expose
    private String registerChannel;


    /**
     * 转换成后台需要的注册json
     * @param map 友盟返回的信息
     * @param isWechat 微信有个特有的unionid，需要特殊处理
     * @return
     */
    public static String toRegisterJson(Map<String, String> map, boolean isWechat) {
        RegisterRequestEntity entity = new RegisterRequestEntity();
        entity.setName(map.get("screen_name"));
        entity.setOpenid(map.get("openid"));
        entity.setSex(map.get("gender"));
        if(isWechat) {
            entity.setUnionid(map.get("unionid"));
        }else {
            entity.setUnionid(entity.getOpenid());
        }
        entity.setIcon(map.get("profile_image_url"));
        entity.setArea(map.get("city"));
        entity.setRegisterChannel(map.get("registerChannel"));
        return JSON.toJSONString(entity);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public void setSex(String sex) {
        if ("男".equals(sex) || "1".equals(sex)) {
            this.sex = 1;
        } else if ("女".equals(sex) || "2".equals(sex)) {
            this.sex = 2;
        }
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getRegisterChannel() {
        return registerChannel;
    }

    public void setRegisterChannel(String registerChannel) {
        this.registerChannel = registerChannel;
    }

    @Override
    public String toString() {
        return "RegisterRequestEntity{" +
                "name='" + name + '\'' +
                ", sex=" + sex +
                ", openid='" + openid + '\'' +
                ", unionid='" + unionid + '\'' +
                ", icon='" + icon + '\'' +
                ", area='" + area + '\'' +
                '}';
    }
}
