package com.sx.dw.accountAndSecurity.entity;

import com.alibaba.fastjson.JSONObject;

/**
 * @Description: vip注册请求参数封装
 * @author: fanjie
 * @date: 2016/12/8 15:27
 */

public class VipRegisterRequestEntity {

    public static final String PHONECODE = "PHONECODE";
    public static final String INFO = "INFO";

    private String code;
    private String format;
    private String registerInfo;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getRegisterInfo() {
        return registerInfo;
    }

    public void setRegisterInfo(String registerInfo) {
        this.registerInfo = registerInfo;
    }

    public String toJsonString(){
        JSONObject object = new JSONObject();
        object.put("code",code);
        object.put("format",format);
        object.put("registerInfo",registerInfo);
        return object.toJSONString();
    }
}
