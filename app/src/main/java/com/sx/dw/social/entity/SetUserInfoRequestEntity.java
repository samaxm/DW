package com.sx.dw.social.entity;

import com.alibaba.fastjson.JSON;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @Description: 设置用户信息请求Json参数封装
 * @author: fanjie
 * @date: 2016/9/28 13:34
 */

public class SetUserInfoRequestEntity {
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("area")
    @Expose
    private String area;
    @SerializedName("sex")
    @Expose
    private int sex;
    @SerializedName("sign")
    @Expose
    private String sign;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
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

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String toJsonString(){
        return JSON.toJSON(this).toString();
    }
}
