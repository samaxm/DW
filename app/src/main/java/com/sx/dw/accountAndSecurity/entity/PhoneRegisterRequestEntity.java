package com.sx.dw.accountAndSecurity.entity;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sx.dw.core.util.AES;
import com.sx.dw.core.util.MD5;

/**
 * @ClassName: ${CLASS_NAME}
 * @Description: 手机注册请求参数封装，提供JSON
 * @author: fanjie
 * @date: 2016/9/22
 */

public class PhoneRegisterRequestEntity {
    @SerializedName("phoneNum")
    @Expose
    private String phoneNum;
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("password")
    @Expose
    private String password;

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password =  MD5.GetMD5Code(password);
    }

    @Override
    public String toString() {
        return "PhoneRegisterRequestEntity{" +
                "phoneNum='" + phoneNum + '\'' +
                ", code='" + code + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public String toJsonString(){
        JSONObject object = new JSONObject();
        object.put("phoneNum",getPhoneNum());
        object.put("code",getCode());
        String passwordAES = AES.encode(getPassword());
        object.put("password",passwordAES);
        return object.toJSONString();
    }
}
