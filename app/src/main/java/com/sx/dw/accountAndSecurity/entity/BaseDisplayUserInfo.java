package com.sx.dw.accountAndSecurity.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sx.dw.im.entity.LinkMan;

import java.io.Serializable;

/**
 * @Description: TODO 添加类的描述
 * @author: fanjie
 * @date: 2017/2/12 12:53
 */

public class BaseDisplayUserInfo implements Serializable {

    @SerializedName("dwID")
    @Expose
    private String dwID;
    @SerializedName("sex")
    @Expose
    private Integer sex;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("icon")
    @Expose
    private String icon;
    @SerializedName("area")
    @Expose
    private String area;
    @SerializedName("sign")
    @Expose
    private String sign;
    @SerializedName("worth")
    @Expose
    private Integer worth;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("tag")
    @Expose
    private String tag;
    @SerializedName("age")
    @Expose
    private String age;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("company")
    @Expose
    private String company;
    @SerializedName("star")
    @Expose
    private String star;
    @SerializedName("realname")
    @Expose
    private String realname;


    public LinkMan toLinkMan(){
        LinkMan man=new LinkMan();
        man.setLike(true);
        man.setIcon(icon);
        man.setWealth(0);
        man.setStar(star);
        man.setTag(tag);
        man.setLikeDate(System.currentTimeMillis());
        man.setAge(age);
        man.setArea(area);
        man.setCompany(company);
        man.setDwID(dwID);
        man.setName(getName());
        man.setWorth(getWorth());
        man.setSex(getSex());
        man.setSign(getSign());
        man.setType(getType());
        man.setRname(realname);
        man.setTitle(title);
        return man;
    }

    public String getDwID() {
        return dwID;
    }

    public void setDwID(String dwID) {
        this.dwID = dwID;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public Integer getWorth() {
        return worth;
    }

    public void setWorth(Integer worth) {
        this.worth = worth;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getStar() {
        return star;
    }

    public void setStar(String star) {
        this.star = star;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }
}
