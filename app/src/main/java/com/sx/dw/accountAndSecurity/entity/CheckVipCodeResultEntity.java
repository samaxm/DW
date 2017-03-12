package com.sx.dw.accountAndSecurity.entity;

/**
 * @Description: 检查vip码的接口返回封装
 * @author: fanjie
 * @date: 2016/12/8 14:49
 */

public class CheckVipCodeResultEntity{

    public static final String UNUSED = "UNUSED";
    public static final String CHECKED = "CHECKED";
    public static final String PAID = "PAID";
    public static final String REGISTER = "REGISTER";

    private String dwid;
    private int id;
    private String partner;
    private String status;

    public String getDwid() {
        return dwid;
    }

    public void setDwid(String dwid) {
        this.dwid = dwid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
